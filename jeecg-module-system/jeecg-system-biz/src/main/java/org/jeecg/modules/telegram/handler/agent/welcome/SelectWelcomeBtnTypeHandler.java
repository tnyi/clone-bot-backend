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
 * 处理代理机器人的"设置欢迎语-设置按钮-选择按钮类型"指令
 */
@Slf4j
@Service
public class SelectWelcomeBtnTypeHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "\uD83D\uDC49 请选择一种需要设置的按钮类型（二选一）：\n" +
                        "\n" +
                        "\uD83D\uDCAC 温馨提示：\n" +
                        "<b>消息按钮</b>：显示在消息底下的按钮，可设置链接来跳转\n" +
                        "<b>底部按钮</b>：显示在输入框下的全局按钮，可引导用户输入关键词";

        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        btnBuilder
                .addRow()
                .addDataButton("\uD83D\uDCCD消息按钮", SetWelcomeMsgBtnHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDD79底部按钮", SetWelcomeBottomBtnHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class)
                .build();

        Message message = update.getCallbackQuery().getMessage();
        BotUtil.setTxtRelyHandler(botAgent.getDbBotId(),  message.getChatId(), SetWelcomeMsgBtnOKHandler.class);
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }

}
