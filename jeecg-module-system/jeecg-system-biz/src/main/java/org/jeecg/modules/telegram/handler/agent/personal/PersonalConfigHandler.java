package org.jeecg.modules.telegram.handler.agent.personal;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.handler.agent.broadcast.SetBroadcastTextOKHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.RegEx;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理代理机器人的"个性化设置"指令
 */
@Slf4j
@Service
public class PersonalConfigHandler extends BaseBotDataHandler {

    @Resource
    private ITgBotService botService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        TgBot tgBot = botService.getById(botAgent.getDbBotId());
        Message message = update.getCallbackQuery().getMessage();
        String data = getCallbackData(update);

        String msg = "⚙️ 个性化设置\n" +
                "\n" +
                "\uD83D\uDC49 <b>聊天模式：</b>\n" +
                "1️⃣ <b>选择模式</b>：bot维持一个最近联系人列表，没有聊天对象。每条消息发送时，需手动选择对象后才转发。\n" +
                "2️⃣ <b>锁定模式</b>：bot保存一个聊天对象，发送消息时直接转发给聊天对象，只有主动切换才会改变聊天对象。\n" +
                "3️⃣ <b>智能模式</b>：在锁定模式下，增加一个判断：当一段时间没有发送消息时，下一条消息会切换至选择模式。\n" +
                "\n" +
                "\uD83D\uDC49 <b>启动消息自动回复</b>：开启后，bot会监控关键词并触发自动回复。\n" +
                "\uD83D\uDC49 <b>同时触发多个回复</b>：开启后，触发符合所有关键词的自动回复\n" +
                "\uD83D\uDC49 <b>显示消息网页预览</b>：开启后，转发消息中的url会显示预览网页。\n" +
                "\uD83D\uDC49 <b>欢迎语的触发提示</b>：开启后，用户发送/start会提示管理者。\n" +
                "\uD83D\uDC49 <b>关键词组触发提示</b>：开启后，用户消息触发关键词会提示管理者。\n" +
                "\uD83D\uDC49 <b>消息发送成功提示</b>：开启后，发送消息成功会提示发送成功。\n" +
                "\uD83D\uDC49 <b>定时清理成功提示</b>：开启后，发送消息成功的提示会定时被删除。\n" +
                "\uD83D\uDC49 <b>群关键词自动回复</b>：开启后，bot会监控群的关键词并自动回复。\n" +
                "\uD83D\uDC49 <b>进群欢迎自动回复</b>：开启后，bot会监控用户进群并自动欢迎。\n" +
                "\uD83D\uDC49 <b>删除上条进群欢迎</b>：开启后，bot自动删除上一条的进群欢迎。\n" +
                "\uD83D\uDC49 <b>自动回复带目标源</b>：开启后，回复带触发关键词的那条消息。";

        if(StringUtils.isBlank(data)) {
            botAgent.sendMsgWithData(message.getChatId(), msg, buildBtnList(tgBot));
        } else {
            if("ChatMode".equals(data)) {
                int nowValue = tgBot.getChatMode();
                if(nowValue >= 1 && nowValue < 3) {
                    nowValue = nowValue + 1;
                } else if(nowValue >= 3){
                    nowValue = 1;
                }
                tgBot.setChatMode(nowValue);
            }
            if("AutoReplyMsg".equals(data)) {
                tgBot.setAutoReplyMsg(tgBot.getAutoReplyMsg()==1 ? 2 : 1);
            }
            if("MutiplyReply".equals(data)) {
                tgBot.setMutiplyReply(tgBot.getMutiplyReply()==1 ? 2 : 1);
            }
            if("SendOkTips".equals(data)) {
                tgBot.setSendOkTips(tgBot.getSendOkTips()==1 ? 2 : 1);
            }
            if("ClearOkTips".equals(data)) {
                int nowValue = tgBot.getClearOkTips();
                if(nowValue >= 1 && nowValue < 3) {
                    nowValue = nowValue + 1;
                } else if(nowValue >= 3){
                    nowValue = 1;
                }
                tgBot.setClearOkTips(nowValue);
            }
            if("DisplayWebPage".equals(data)) {
                tgBot.setDisplayWebPage(tgBot.getDisplayWebPage()==1 ? 2 : 1);
            }
            if("WelcomeMsgTips".equals(data)) {
                tgBot.setWelcomeMsgTips(tgBot.getWelcomeMsgTips()==1 ? 2 : 1);
            }
            if("KeywordTips".equals(data)) {
                tgBot.setKeywordTips(tgBot.getKeywordTips()==1 ? 2 : 1);
            }
            if("GroupAutoReply".equals(data)) {
                tgBot.setGroupAutoReply(tgBot.getGroupAutoReply()==1 ? 2 : 1);
            }
            if("JoinAutoReply".equals(data)) {
                tgBot.setJoinAutoReply(tgBot.getJoinAutoReply()==1 ? 2 : 1);
            }
            if("DelLastJoin".equals(data)) {
                tgBot.setDelLastJoin(tgBot.getDelLastJoin()==1 ? 2 : 1);
            }
            if("ReplyWithTarget".equals(data)) {
                tgBot.setReplyWithTarget(tgBot.getReplyWithTarget()==1 ? 2 : 1);
            }
            botService.updateById(tgBot);
            botAgent.sendEditMsg(message.getChatId(), msg, buildBtnList(tgBot), message.getMessageId());
        }
        callbackDataService.del(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + message.getChatId());
    }

    private ArrayList<List<InlineButtonVO>> buildBtnList(TgBot tgBot) {
        String[] chatModeArr = {"选择模式", "锁定模式", "智能模式"};
        String[] clearOKArr = {"永不", "3秒", "5秒"};
        Class handlerClass = PersonalConfigHandler.class;
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        return btnBuilder
                .addRow()
                .addDataButton("\uD83D\uDDE3️聊天模式：" + chatModeArr[tgBot.getChatMode()-1], "ChatMode", handlerClass)
                .addRow()
                .addDataButton((tgBot.getAutoReplyMsg()==1 ? "✅" : "⬜️") + " 启动消息自动回复", "AutoReplyMsg", handlerClass)
                .addDataButton((tgBot.getMutiplyReply()==1 ? "✅" : "⬜️") + " 同时触发多个回复", "MutiplyReply", handlerClass)
                .addRow()
                .addDataButton((tgBot.getSendOkTips()==1 ? "✅" : "⬜️") + " 消息发送成功提示", "SendOkTips", handlerClass)
                .addDataButton("定时清理："+clearOKArr[tgBot.getClearOkTips()-1]+"[点击切换]", "ClearOkTips", handlerClass)
                .addRow()
                .addDataButton((tgBot.getDisplayWebPage()==1 ? "✅" : "⬜️") + " 显示消息网页预览", "DisplayWebPage", handlerClass)
                .addRow()
                .addDataButton((tgBot.getWelcomeMsgTips()==1 ? "✅" : "⬜️") + " 欢迎语的触发提示", "WelcomeMsgTips", handlerClass)
                .addDataButton((tgBot.getKeywordTips()==1 ? "✅" : "⬜️") + " 关键词组触发提示", "KeywordTips", handlerClass)
                .addRow()
                .addDataButton((tgBot.getGroupAutoReply()==1 ? "✅" : "⬜️") + " 群关键词自动回复", "GroupAutoReply", handlerClass)
                .addDataButton((tgBot.getJoinAutoReply()==1 ? "✅" : "⬜️") + " 进群欢迎语自动回复", "JoinAutoReply", handlerClass)
                .addRow()
                .addDataButton((tgBot.getDelLastJoin()==1 ? "✅" : "⬜️") + " 删除上条进群欢迎", "DelLastJoin", handlerClass)
                .addDataButton((tgBot.getReplyWithTarget()==1 ? "✅" : "⬜️") + " 自动回复带目标源", "ReplyWithTarget", handlerClass)
                .addRow()
                .addDataButton("返回", BackToMainHandler.class)
                .build();
    }

}
