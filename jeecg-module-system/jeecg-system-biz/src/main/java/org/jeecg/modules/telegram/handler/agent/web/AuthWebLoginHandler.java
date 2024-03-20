package org.jeecg.modules.telegram.handler.agent.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.entity.TgWebDevice;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.handler.agent.ViewEmptyBtnHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.service.ITgWebDeviceService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.websocket.IMWebSocket;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理代理机器人的"Web网页登录授权"
 */
@Slf4j
@Service
public class AuthWebLoginHandler extends BaseBotDataHandler {

    @Resource
    private ITgChatService chatService;

    @Resource
    private ITgWebDeviceService webDeviceService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private IMWebSocket imWebSocket;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = getCallbackData(update);   //生成的唯一设备号
        String msg = "✅授权登录完成";

        String json = callbackDataService.get(BotRedisConstant.WEB_DEVICE_INFO + data);
        if(json == null) {
            log.error("设备信息不存在");
            return;
        }

        List<TgWebDevice> onlineDeviceList = webDeviceService.lambdaQuery()
                                                                .eq(TgWebDevice::getBotId, botAgent.getDbBotId())
                                                                .eq(TgWebDevice::getStatus, 1)
                                                                .orderByDesc(TgWebDevice::getCreateTime)
                                                                .last("limit 3").list();
        if(onlineDeviceList.size() == 3) {
            //超过3台，则将最后一台踢下线
            TgWebDevice lastDevice = onlineDeviceList.get(2);
            lastDevice.setStatus(2);
            webDeviceService.updateById(lastDevice);
            onlineDeviceList.remove(lastDevice);

            JSONObject wsMsg = new JSONObject();
            wsMsg.put("cmd", 2);
            imWebSocket.pushMessage(lastDevice.getDeviceNo(), wsMsg.toJSONString());
        }

        TgWebDevice webDevice = webDeviceService.lambdaQuery().eq(TgWebDevice::getDeviceNo, data).one();
        if(webDevice == null) {
            webDevice = JSON.parseObject(json, TgWebDevice.class);
            webDevice.setStatus(1);
            webDeviceService.save(webDevice);
        } else {
            webDevice.setStatus(1);
            webDeviceService.updateById(webDevice);
        }

        //将历史记录中还在线状态的，全部踢下线
        onlineDeviceList.add(webDevice);
        List<String> onlineIdList = onlineDeviceList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<TgWebDevice> hisDeviceList = webDeviceService.lambdaQuery()
                                            .eq(TgWebDevice::getBotId, botAgent.getDbBotId())
                                            .notIn(TgWebDevice::getId, onlineIdList)
                                            .eq(TgWebDevice::getStatus, 1).list();
        for(TgWebDevice device : hisDeviceList) {
            JSONObject wsMsg = new JSONObject();
            wsMsg.put("cmd", 2);
            imWebSocket.pushMessage(device.getDeviceNo(), wsMsg.toJSONString());
        }
        webDeviceService.lambdaUpdate()
                        .eq(TgWebDevice::getBotId, botAgent.getDbBotId())
                        .notIn(TgWebDevice::getId, onlineIdList)
                        .eq(TgWebDevice::getStatus, 1)
                        .set(TgWebDevice::getStatus, 2).update();

        HashMap<String, String> chatInfoMap = new HashMap<>();
        List<TgChat> tgChatList = chatService.lambdaQuery().eq(TgChat::getBotId, botAgent.getDbBotId()).eq(TgChat::getChatType, 1).list();
        for(TgChat chat : tgChatList) {
            String adminId = chat.getAdminId();
            String chatId = chat.getChatId();
            GetChatMember getChatMember = GetChatMember.builder()
                    .chatId(Long.parseLong(chatId)) // 聊天的唯一标识符
                    .userId(Long.parseLong(adminId)) // 目标用户的唯一标识符
                    .build();
            try {
                ChatMember chatMember = botAgent.execute(getChatMember);
                if (chatMember != null) {
                    chatInfoMap.put(chatId, BotUtil.getFullname(chatMember.getUser()));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        //通知IM前端授权成功
        JSONObject wsMsg = new JSONObject();
        JSONArray dataList = new JSONArray();
        wsMsg.put("cmd", 200);
        chatInfoMap.forEach((key, value) -> {
            JSONObject dataItem = new JSONObject();
            dataItem.put("userName", key);
            dataItem.put("nickName", value);
            dataList.add(dataItem);
        });
        JSONObject dataObj = new JSONObject();
        dataObj.put("chatList", dataList);
        dataObj.put("botId", botAgent.getDbBotId());
        wsMsg.put("data", dataObj);
        imWebSocket.pushMessage(data, wsMsg.toJSONString());

        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        ArrayList<List<InlineButtonVO>> butList = btnBuilder
                .addRow()
                .addDataButton(msg, ViewEmptyBtnHandler.class)
                .build();
        botAgent.sendEditMsg(message.getChatId(), message.getText(), butList, message.getMessageId());
    }

}
