package org.jeecg.modules.telegram.handler.agent.autoreply;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 处理代理机器人的只有文本提示的按钮点击
 */
@Slf4j
@Service
public class ViewReplyConfigHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
        answerCallbackQuery.setText("⚠️自动回复未生效\n" +
                                    "\uD83D\uDCCC已设置关键词组\n" +
                                    "✏️已设置回复文本\n" +
                                    "\uD83D\uDDBC已设置媒体文件\n" +
                                    "\uD83D\uDCCD已设置消息按钮\n" +
                                    "\uD83D\uDD0D精确匹配\n" +
                                    "\uD83D\uDD18模糊匹配\n" +
                                    "\uD83E\uDDD1\u200D\uD83D\uDCBC仅管理员可触发");  // 设置提示文本
        answerCallbackQuery.setShowAlert(true);  // 设置为true以显示一个警告，而不是一个通知
        botAgent.execute(answerCallbackQuery);
        return;
    }

}
