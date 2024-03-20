package org.jeecg.modules.telegram.handler.agent.welcome;

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
 * 处理代理机器人的"设置欢迎语-设置按钮-设置消息按钮"指令
 */
@Slf4j
@Service
public class SetWelcomeMsgBtnHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "\uD83D\uDC49 请回复按钮内容，格式如下：\n" +
                        "\n" +
                        "<code>按钮1 - 链接1\n" +
                        "按钮2 - 链接2 | 按钮3 - 链接3\n" +
                        "按钮4 - 链接4</code>\n" +
                        "\n" +
                        "\uD83D\uDCAC 温馨提示：\n" +
                        "1️⃣ 一行文本即一行按钮，同一行多个按钮通过 | 分割\n" +
                        "2️⃣ 链接 需要 <b>http、https、tg</b> 开头";

        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        btnBuilder
                .addRow()
                .addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class)
                .build();

        Message message = update.getCallbackQuery().getMessage();
        BotUtil.setTxtRelyHandler(botAgent.getDbBotId(),  message.getChatId(), SetWelcomeMsgBtnOKHandler.class);
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }

}