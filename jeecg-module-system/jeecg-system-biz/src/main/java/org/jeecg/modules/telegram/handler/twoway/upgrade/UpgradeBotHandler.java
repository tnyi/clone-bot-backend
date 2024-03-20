package org.jeecg.modules.telegram.handler.twoway.upgrade;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 处理双向机器人的"升级专业版"指令
 */
@Slf4j
@Service
public class UpgradeBotHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        System.out.println(update.getCallbackQuery().getMessage().getFrom().getId());
        botAgent.sendMsg(message.getChatId(), "升级专业版请联系管理员<b><a href=\"https://t.me/frankmartinez999\">@BotMaster</a></b>");
    }

}
