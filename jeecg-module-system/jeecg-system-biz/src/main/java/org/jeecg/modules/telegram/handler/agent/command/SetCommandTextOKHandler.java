package org.jeecg.modules.telegram.handler.agent.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.jeecg.modules.telegram.bot.BaseBot;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 处理代理机器人的"设置指令-成功"指令
 */
@Slf4j
@Service
public class SetCommandTextOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        if(StringUtils.isBlank(message.getText())) {
            return;
        }

        TgBot bot = botService.getById(botAgent.getDbBotId());
        bot.setCommandList(message.getText());
        botService.updateById(bot);

        Set<BotCommand> botCommandList = new HashSet<>();
        String[] rowStrList = StringUtils.split( message.getText(), "\n");
        for(String row : rowStrList) {
            String[] cmdList = row.split("-");
            if(cmdList.length != 2) {
                continue;
            }
            BotCommand botCommand = new BotCommand();
            botCommand.setCommand("/" + cmdList[0].trim());
            botCommand.setDescription(cmdList[1].trim());
            botCommandList.add(botCommand);
        }
        SetMyCommands setMyCommands = SetMyCommands.builder().commands(botCommandList).build();
        botAgent.execute(setMyCommands);

        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        String msg = "✅ 指令设置完成\n" +
                "\n" + message.getText();
        botAgent.sendMsgWithData(message.getChatId(), msg, BotUtil.configCommand());
    }

}
