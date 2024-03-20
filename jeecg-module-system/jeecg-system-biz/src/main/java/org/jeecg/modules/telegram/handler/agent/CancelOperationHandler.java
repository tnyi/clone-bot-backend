package org.jeecg.modules.telegram.handler.agent;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

/**
 * 处理代理机器人的"设置欢迎语-设置文本-取消"指令
 */
@Slf4j
@Service
public class CancelOperationHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "\uD83D\uDEAB 已取消操作";
        Message message = update.getCallbackQuery().getMessage();
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        botAgent.sendEditMsg(message.getChatId(), msg, new ArrayList<>(), message.getMessageId());
    }

}
