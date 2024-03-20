package org.jeecg.modules.telegram.handler.agent.autoreply;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置自动回复-预览"指令
 */
@Slf4j
@Service
public class PreviewAutoReplyMsgHandler extends BaseBotDataHandler {

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String configName = getCallbackData(update);
        TgAutoReplyConfig config = autoReplyConfigService.getByConfigName(botAgent.getDbBotId(), configName);

        if(config == null || StringUtils.isBlank(config.getTextMsg())) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
            answerCallbackQuery.setText("⚠️请先设置文本内容!");  // 设置提示文本
            answerCallbackQuery.setShowAlert(true);  // 设置为true以显示一个警告，而不是一个通知
            botAgent.execute(answerCallbackQuery);
            return;
        }
        BotUtil.sendTextMsg(botAgent, chatId, config.getMediaType(), config.getTextMsg(), config.getMsgButton(), config.getMediaFile());
    }

}
