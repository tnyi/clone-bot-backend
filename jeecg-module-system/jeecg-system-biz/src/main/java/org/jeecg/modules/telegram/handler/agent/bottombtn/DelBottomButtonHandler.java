package org.jeecg.modules.telegram.handler.agent.bottombtn;

import lombok.extern.slf4j.Slf4j;
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
 * 处理代理机器人的"设置底部按钮-删除"指令
 */
@Slf4j
@Service
public class DelBottomButtonHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        TgBot bot = botService.getById(botAgent.getDbBotId());
        bot.setBottomButton(null);
        botService.lambdaUpdate().set(TgBot::getBottomButton, null).eq(TgBot::getId, bot.getId()).update();

        String msg = "✅ 清空底部按钮完成";
        botAgent.sendEditMsg(message.getChatId(), msg, BotUtil.configBottomButton(), message.getMessageId());
    }

}
