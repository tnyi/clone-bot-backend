package org.jeecg.modules.telegram.handler.agent.welcome;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 处理代理机器人的"设置欢迎语-预览"指令
 */
@Slf4j
@Service
public class PreviewWelcomeMsgHandler extends BaseBotDataHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        TgWelcomeConfig config = welcomeConfigService.getBotWelcomeByConfigId(getCallbackData(update));
        String txt = config != null ? config.getTextMsg() : null;
        if(config == null || StringUtils.isBlank(txt)) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
            answerCallbackQuery.setText("⚠️请先设置文本内容!");  // 设置提示文本
            answerCallbackQuery.setShowAlert(true);  // 设置为true以显示一个警告，而不是一个通知
            botAgent.execute(answerCallbackQuery);
            return;
        }

        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        if(config.getButtonType() == null || config.getButtonType() == 1) {
            BotUtil.sendTextMsg(botAgent, chatId, config.getMediaType(), config.getTextMsg(), config.getMsgButton(), config.getMediaFile());
        } else {
            Set<KeyboardRow> keyboardRowSet = BotUtil.parseBottomBtn(config.getMsgButton());
            ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder().keyboard(keyboardRowSet).resizeKeyboard(true).build();
            BotUtil.sendTextMsg(botAgent, chatId, config.getMediaType(), config.getTextMsg(), "", config.getMediaFile(), null, replyKeyboardMarkup);
        }
    }

}
