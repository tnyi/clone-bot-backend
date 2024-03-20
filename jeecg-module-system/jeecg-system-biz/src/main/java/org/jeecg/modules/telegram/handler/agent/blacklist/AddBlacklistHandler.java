package org.jeecg.modules.telegram.handler.agent.blacklist;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.handler.agent.autoreply.AddAutoReplyOKHandler;
import org.jeecg.modules.telegram.handler.agent.msgtemplate.SetMsgTemplateHandler;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理代理机器人的"黑名单-拉黑用户"指令
 */
@Slf4j
@Service
public class AddBlacklistHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "\uD83D\uDC49 请回复需要拉黑的用户id：";
        Message message = update.getCallbackQuery().getMessage();
        BotUtil.setTxtRelyHandler(botAgent.getDbBotId(),  message.getChatId(), AddBlacklistOKHandler.class);
        ArrayList<List<InlineButtonVO>> butList = InlineButtonVO.builder().addRow().addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class).build();
        botAgent.sendMsgWithData(message.getChatId(), msg, butList);
    }

}
