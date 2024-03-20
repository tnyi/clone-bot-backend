package org.jeecg.modules.telegram.handler.agent.bottombtn;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置底部按钮"指令
 */
@Slf4j
@Service
public class SetBottomButtonHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        String botId = botAgent.getDbBotId();
        TgBot bot = botService.getById(botId);
        String msg = null;
        if(StringUtils.isNotBlank(bot.getBottomButton())) {
            msg = "\uD83D\uDD79 设置底部按钮，当前设置如下：\n" +
                    "\n" +
                    bot.getBottomButton();
        } else {
            msg = "\uD83D\uDD79 设置底部按钮，当前未设置";
        }
        Message message = update.getCallbackQuery().getMessage();
        botAgent.sendEditMsg(message.getChatId(), msg, BotUtil.configBottomButton(), message.getMessageId());
    }

}
