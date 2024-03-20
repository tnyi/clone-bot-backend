package org.jeecg.modules.telegram.handler.agent.botinfo;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.handler.agent.autoreply.SetAutoReplyKeywordOKHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"修改名字"指令
 */
@Slf4j
@Service
public class EditBotNameHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        TgBot tgBot = botService.getById(botAgent.getDbBotId());
        String msg = "⁉️ <b>如何修改机器人的名字？</b>\n\n" +
                "1.打开<b><a href=\"https://t.me/BotFather\">@BotFather</a></b>并点击\"Start\"\n" +
                "2.发送/mybots指令\n" +
                "3.点击@" + tgBot.getBotName() + "按钮\n" +
                "4.点击 <b>Edit Bot</b> 按钮\n" +
                "5.点击 <b>Edit Name</b> 按钮\n" +
                "6.发送这个Bot的<b>新名字</b>";

        Message message = update.getCallbackQuery().getMessage();
        Message sendMsg = botAgent.sendMsg(message.getChatId(), msg);
        BotUtil.setNotNeedSend(sendMsg);
    }

}
