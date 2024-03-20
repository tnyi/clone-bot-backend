package org.jeecg.modules.telegram.handler.twoway.botmgr;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.bot.TwoWayBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 处理"添加机器人"按钮点击事件
 */
@Slf4j
@Service
public class CancelAddBotCallbackHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot baseBot, Update update) {
        log.info("取消添加机器人");
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        BotUtil.delTxtRelyHandler(baseBot.getDbBotId(), chatId);

        StringBuilder replyTextSB = new StringBuilder();
        replyTextSB.append("\uD83D\uDEAB 已取消操作");

        SendMessage replyMsg = SendMessage.builder()
                .text(replyTextSB.toString())
                .chatId(chatId)
                .parseMode("html")
                .build();
        TwoWayBot.instance().execute(replyMsg);
    }

}
