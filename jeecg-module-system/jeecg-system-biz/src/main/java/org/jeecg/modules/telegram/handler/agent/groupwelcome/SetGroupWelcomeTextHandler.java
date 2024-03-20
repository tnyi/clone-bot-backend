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
 * 处理代理机器人的"设置进群欢迎语-设置文本"指令
 */
@Slf4j
@Service
public class SetGroupWelcomeTextHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "\uD83D\uDC49 请回复需要设置的文本内容：\n" +
                        "\n" +
                        "\uD83D\uDCAC 温馨提示：\n" +
                        "回复的文本可直接带格式，同时支持HTML标签。\n" +
                        "\n" +
                        "目前支持以下变量：\n" +
                        "<code>{id}</code>=用户id\n" +
                        "<code>{id_code}</code>=可复制的用户id\n" +
                        "<code>{display_name}</code>=用户姓名\n" +
                        "<code>{mention}</code>=用户姓名账号链接\n" +
                        "<code>{username}</code>=@用户名\n" +
                        "<code>{group_id}</code>=群组id\n" +
                        "<code>{group_id_code}</code>=可复制的群组id\n" +
                        "<code>{group_name}</code>=群组名字";

        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        btnBuilder
                .addRow()
                .addDataButton("\uD83D\uDC49html标签帮助", HtmlTagHelpHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class)
                .build();

        Message message = update.getCallbackQuery().getMessage();
        BotUtil.setTxtRelyHandler(botAgent.getDbBotId(),  message.getChatId(), SetGroupWelcomeTextOKHandler.class);
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }

}
