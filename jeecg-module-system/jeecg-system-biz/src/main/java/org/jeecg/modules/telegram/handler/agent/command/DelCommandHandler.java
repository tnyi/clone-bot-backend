package org.jeecg.modules.telegram.handler.agent.command;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置指令-删除"指令
 */
@Slf4j
@Service
public class DelCommandHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        TgBot bot = botService.getById(botAgent.getDbBotId());
        bot.setCommandList(null);
        botService.lambdaUpdate().set(TgBot::getCommandList, null).eq(TgBot::getId, bot.getId()).update();

        BotCommand botCommand = new BotCommand();
        botCommand.setCommand("/start");
        botCommand.setDescription("start bot");
        SetMyCommands setMyCommands = SetMyCommands.builder().command(botCommand).build();
        botAgent.execute(setMyCommands);

        String msg = "✅ 还原指令完成";
        botAgent.sendEditMsg(message.getChatId(), msg, BotUtil.configCommand(), message.getMessageId());
    }

}
