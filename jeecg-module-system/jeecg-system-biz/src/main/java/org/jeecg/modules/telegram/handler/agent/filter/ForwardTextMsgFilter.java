package org.jeecg.modules.telegram.handler.agent.filter;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.config.WebIMConfig;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.*;
import org.jeecg.modules.telegram.handler.BotMsgFilter;
import org.jeecg.modules.telegram.handler.BotMsgFilterChain;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.handler.agent.sendmsg.SelectRecvUserHandler;
import org.jeecg.modules.telegram.service.*;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.vo.SendMsgFromTGDTO;
import org.jeecg.modules.telegram.websocket.IMWebSocket;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 转发文本消息给管理员
 */
@Slf4j
@Service
public class ForwardTextMsgFilter implements BotMsgFilter {

    @Resource
    private ITgChatService tgChatService;

    @Resource
    private ITgWebDeviceService webDeviceService;

    @Resource
    private WebIMConfig webIMConfig;

    @Resource
    private ITgBotService botService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgBlacklistService blacklistService;

    @Resource
    private ITgBotAdminService botAdminService;

    @Resource
    private IMWebSocket imWebSocket;

    @Override
    public void doFilter(BaseBot botAgent, Update update, BotMsgFilterChain filterChain) {
        if(!filterChain.couldForward()) {
            //特殊消息无需转发
            filterChain.doFilter(botAgent, update);
            return;
        }

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String textHandler = callbackDataService.get(BotRedisConstant.BIZ_REPLY_CHAT_ID + chatId);
        if(StringUtils.isNotBlank(textHandler)) {
            //有具体业务处理的消息，无需转发
            filterChain.doFilter(botAgent, update);
            return;
        }

        TgChat fromChat = tgChatService.lambdaQuery()
                                .eq(TgChat::getBotId, botAgent.getDbBotId())
                                .eq(TgChat::getChatId, chatId).one();
        if(fromChat.getChatType() == 2) {
            //群聊消息无需转发
            filterChain.doFilter(botAgent, update);
            return;
        }

        TgChat adminChat = tgChatService.lambdaQuery()
                                .eq(TgChat::getBotId, botAgent.getDbBotId())
                                .eq(TgChat::getChatType, 1)
                                .eq(TgChat::getAdminChat, 1)
                                .last("limit 1").one();
        if(adminChat == null) {
            log.error("数据异常，没有可以转发的聊天信息");
            filterChain.doFilter(botAgent, update);
            return;
        }

        List<TgBotAdmin> botAdminList = botAdminService.lambdaQuery().eq(TgBotAdmin::getBotId, botAgent.getDbBotId()).list();
        List<String> adminChatIdList = botAdminList.stream().map(e -> e.getChatId()).collect(Collectors.toList());
        if(adminChatIdList==null || adminChatIdList.size()==0) {
            adminChatIdList.add(adminChat.getChatId());
        }

        Long forwardFromAdminChatId = Long.parseLong(adminChat.getChatId());
        //判断是否是管理员发送的消息
        if(chatId.equals(forwardFromAdminChatId) || adminChatIdList.contains(chatId.toString())) {
            if(BotUtil.getNotNeedSend(update)) {
                //管理员的特殊的消息无需转发
                filterChain.doFilter(botAgent, update);
                return;
            }

            TgBot tgBot = botService.getById(botAgent.getDbBotId());
            //处理管理员单独回复的消息
            Message adminReplyMsg = message.getReplyToMessage();
            if(adminReplyMsg != null) {
                User forwardUser = adminReplyMsg.getForwardFrom();
                if(forwardUser != null) {
                    TgChat tgChat = tgChatService.lambdaQuery()
                            .eq(TgChat::getBotId, botAgent.getDbBotId())
                            .eq(TgChat::getChatType, 1)
                            .eq(TgChat::getAdminChat, 2)
                            .eq(TgChat::getAdminId, forwardUser.getId())
                            .last("limit 1")
                            .one();
                    replyToUser(tgBot, botAgent, chatId, tgChat, message);

                    filterChain.doFilter(botAgent, update);
                    return;
                }
            }

            //检查是否有可以转发的会话
            List<TgChat> tgChatList = tgChatService.lambdaQuery()
                                        .eq(TgChat::getBotId, botAgent.getDbBotId())
                                        .eq(TgChat::getChatType, 1)
                                        .eq(TgChat::getAdminChat, 2)
                                        .notIn(TgChat::getChatId, adminChatIdList)
                                        .list();
            if(tgChatList.size() == 0) {
                filterChain.doFilter(botAgent, update);
                return;
            }

            int chatMode = tgBot.getChatMode();
            if(chatMode == 1) {
                //选择模式
                selectRecvUser(botAgent, tgChatList, chatId, message);
            }
            else if(chatMode == 2) {
                //锁定模式
                if(StringUtils.isBlank(adminChat.getMsgRecieverId())) {
                     selectRecvUser(botAgent, tgChatList, chatId, message);
                } else {
                    sendToFixedUser(tgBot, botAgent, tgChatList, adminChat, message);
                }
            }
            else if(chatMode == 3) {
                //智能模式
                if(StringUtils.isBlank(adminChat.getMsgRecieverId())) {
                    selectRecvUser(botAgent, tgChatList, chatId, message);
                } else {
                    Date now = new Date();
                    Date lastMsgTime = adminChat.getLastMsgTime();
                    if(now.getTime() - lastMsgTime.getTime() > 30 * 60 * 1000) {
                        //如果超过30分钟没有发送消息，还是要选择消息接收人
                        selectRecvUser(botAgent, tgChatList, chatId, message);
                    } else {
                        sendToFixedUser(tgBot, botAgent, tgChatList, adminChat, message);
                    }
                }
            }
            //用于机器人配置的聊天会话无需转发给管理员自己
            filterChain.doFilter(botAgent, update);
            return;
        }
        //转发给机器人管理员
        ForwardMessage forwardMessage = ForwardMessage.builder().fromChatId(chatId).messageId(message.getMessageId()).chatId(forwardFromAdminChatId).build();
        botAgent.execute(forwardMessage);

        //发给其他管理员
        for(TgBotAdmin admin : botAdminList) {
            if(StringUtils.isNotBlank(admin.getChatId())) {
                ForwardMessage otherMessage = ForwardMessage.builder().fromChatId(chatId).messageId(message.getMessageId()).chatId(admin.getChatId()).build();
                botAgent.execute(otherMessage);
            }
        }

        //检查当前是否有网页版登录设备，有则转发消息
        sendToWeb(botAgent, chatId, message.getText());

        filterChain.doFilter(botAgent, update);
    }

    /**
     * 检查当前是否有网页版登录设备，有则转发消息
     * @param botAgent
     * @param chatId
     * @param msg
     */
    private void sendToWeb(BaseBot botAgent, Long chatId, String msg) {
        List<TgWebDevice> webDeviceList = webDeviceService.lambdaQuery().eq(TgWebDevice::getBotId, botAgent.getDbBotId()).eq(TgWebDevice::getStatus, 1).list();
        for(TgWebDevice webDevice : webDeviceList) {
            SendMsgFromTGDTO sendMsgFromTGDTO = new SendMsgFromTGDTO();
            sendMsgFromTGDTO.setFromUserName(String.valueOf(chatId));
            sendMsgFromTGDTO.setRecvUserName(webDevice.getDeviceNo());
            sendMsgFromTGDTO.setType(0);
            sendMsgFromTGDTO.setContent(msg);
            try {
                String ret = HttpUtil.createPost(webIMConfig.getDomain() + "/message/private/sendFromTG")
                        .body(JSON.toJSONString(sendMsgFromTGDTO), "application/json")
                        .execute().body();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 检查当前是否有网页版登录设备，有则转发消息
     * @param botAgent
     * @param chatId
     * @param msg
     */
    private void sendToWeb(BaseBot botAgent, Long chatId, String targetChatId, String msg) {
        List<TgWebDevice> webDeviceList = webDeviceService.lambdaQuery().eq(TgWebDevice::getBotId, botAgent.getDbBotId()).eq(TgWebDevice::getStatus, 1).list();
        for(TgWebDevice webDevice : webDeviceList) {
            SendMsgFromTGDTO sendMsgFromTGDTO = new SendMsgFromTGDTO();
            sendMsgFromTGDTO.setFromUserName(webDevice.getDeviceNo());
            sendMsgFromTGDTO.setRecvUserName(targetChatId);
            sendMsgFromTGDTO.setType(0);
            sendMsgFromTGDTO.setContent(msg);
            try {
                String ret = HttpUtil.createPost(webIMConfig.getDomain() + "/message/private/sendFromTG")
                        .body(JSON.toJSONString(sendMsgFromTGDTO), "application/json")
                        .execute().body();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            JSONObject wsMsg = new JSONObject();
            wsMsg.put("cmd", 3);
            imWebSocket.pushMessage(webDevice.getDeviceNo(), wsMsg.toJSONString());
        }
    }

    /**
     * 让管理员选择消息接收者
     * @param botAgent
     * @param tgChatList
     * @param chatId
     * @param message
     */
    private void selectRecvUser(BaseBot botAgent, List<TgChat> tgChatList, long chatId, Message message) {
//        String msg = "\uD83D\uDC49 请选择这条消息要转发的人：";
//        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
//        for(TgChat chat : tgChatList) {
//            btnBuilder.addRow().addDataButton(chat.getAdminUsername(), chat.getAdminId(), SelectRecvUserHandler.class);
//        }
//        btnBuilder.addRow().addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class);
//        botAgent.sendMsgWithData(chatId, msg, btnBuilder.build(), message.getMessageId());
//
//        //缓存当前消息内容，待选择接收用户后立即发送消息
//        HashMap dataMap = new HashMap();
//        dataMap.put("msgId", message.getMessageId());
//        dataMap.put("msgText", message.getText());
//        dataMap.put("fromChatId", message.getChatId());
//        callbackDataService.set(BotRedisConstant.ADMIN_SEND_MSG + chatId, JSON.toJSONString(dataMap));

        String msg = "✅请你选择引用@艾特信息回复给指定的用户！";
        botAgent.sendMsg(chatId, msg);
    }

    /**
     * 发消息给锁定的接收者
     * @param tgBot
     * @param botAgent
     * @param tgChatList
     * @param adminChat
     * @param message
     */
    private void sendToFixedUser(TgBot tgBot, BaseBot botAgent, List<TgChat> tgChatList, TgChat adminChat, Message message) {
        if(StringUtils.isBlank(message.getText())) {
            return;
        }
        TgChat forwardChat = null;
        for (TgChat chat : tgChatList) {
            if (adminChat.getMsgRecieverId().equals(chat.getAdminId())) {
                forwardChat = chat;
                break;
            }
        }
        botAgent.sendMsg(Long.parseLong(forwardChat.getChatId()), BotUtil.checkAdTail(tgBot, message.getText()));
        adminChat.setLastMsgTime(new Date());
        tgChatService.updateById(adminChat);

        if(tgBot.getSendOkTips() == 1) {
            //消息发送成功后，提醒管理员
            String tips = getTips(botAgent, forwardChat);
            botAgent.sendReplyMsg(Long.parseLong(adminChat.getChatId()), message.getMessageId(), tips);
        }
    }

    /**
     * 回复消息给指定的接收者
     * @param tgBot
     * @param botAgent
     * @param tgChatList
     * @param adminChat
     * @param message
     */
    private void replyToUser(TgBot tgBot, BaseBot botAgent, Long fromChatId, TgChat targetChat, Message message) {
        if(StringUtils.isBlank(message.getText())) {
            return;
        }
        TgChat forwardChat = targetChat;
        botAgent.sendMsg(Long.parseLong(forwardChat.getChatId()), BotUtil.checkAdTail(tgBot, message.getText()));
        targetChat.setLastMsgTime(new Date());
        tgChatService.updateById(targetChat);

        //检查当前是否有网页版登录设备，有则转发消息
//        sendToWeb(botAgent, fromChatId, message.getText());
        sendToWeb(botAgent, fromChatId, targetChat.getChatId(), message.getText());

        if(tgBot.getSendOkTips() == 1) {
            //消息发送成功后，提醒管理员
            String tips = getTips(botAgent, forwardChat);
            botAgent.sendReplyMsg(fromChatId, message.getMessageId(), tips);
        }
    }

    private String getTips(BaseBot botAgent, TgChat forwardChat) {
        GetChatMember getChatMember = GetChatMember.builder().chatId(forwardChat.getChatId()).userId(Long.parseLong(forwardChat.getAdminId())).build();
        ChatMember chatMember = botAgent.execute(getChatMember);
        String tips = "\uD83D\uDCAC 消息已成功发送至 <b><a href=\"https://t.me/" + forwardChat.getAdminUsername() + "\">" + BotUtil.getFullname(chatMember.getUser()) + "</a></b>\n" +
                "\n" +
                "(该提示消息可在 个性化设置-->消息发送成功提示 中关闭)";
        return tips;
    }

}
