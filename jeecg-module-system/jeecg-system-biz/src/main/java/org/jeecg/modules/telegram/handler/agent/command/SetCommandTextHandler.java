package org.jeecg.modules.telegram.handler.agent.command;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 处理代理机器人的"设置自动回复-设置按钮"指令
 */
@Slf4j
@Service
public class SetCommandTextHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "\uD83D\uDC49 请回复要需要设置的指令，格式如下：\n" +
                        "\n" +
                        "指令1 - 描述1\n" +
                        "指令2 - 描述2\n" +
                        "\n" +
                        "例如：\n" +
                        "start - 开始使用\n" +
                        "test - 测试\n" +
                        "\n" +
                        "注：指令只能是数字和英文，描述可以中文";

        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        btnBuilder
                .addRow()
                .addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class)
                .build();

        Message message = update.getCallbackQuery().getMessage();
        BotUtil.setTxtRelyHandler(botAgent.getDbBotId(),  message.getChatId(), SetCommandTextOKHandler.class);
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }

}
