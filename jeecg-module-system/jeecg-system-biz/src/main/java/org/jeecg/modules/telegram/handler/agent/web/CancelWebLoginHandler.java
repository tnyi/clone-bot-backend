package org.jeecg.modules.telegram.handler.agent.web;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.ViewEmptyBtnHandler;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理代理机器人的"网页版登录授权-取消"指令
 */
@Slf4j
@Service
public class CancelWebLoginHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "\uD83D\uDEAB 已取消操作";
        Message message = update.getCallbackQuery().getMessage();
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());

        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        ArrayList<List<InlineButtonVO>> butList = btnBuilder
                .addRow()
                .addDataButton(msg, ViewEmptyBtnHandler.class)
                .build();
        botAgent.sendEditMsg(message.getChatId(), message.getText(), butList, message.getMessageId());
    }

}
