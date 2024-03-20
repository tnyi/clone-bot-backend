package org.jeecg.modules.telegram.handler.agent.groupwelcome;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.handler.agent.HtmlTagHelpHandler;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 处理代理机器人的"设置进群欢迎语-设置欢迎语保留时间"指令
 */
@Slf4j
@Service
public class SetGroupWelcomeSaveTimeHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "请回复该欢迎语保留的时间(单位为秒)：";
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        btnBuilder
                .addRow()
                .addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class)
                .build();

        Message message = update.getCallbackQuery().getMessage();
        BotUtil.setTxtRelyHandler(botAgent.getDbBotId(),  message.getChatId(), SetGroupWelcomeSaveTimeOKHandler.class);
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }

}
