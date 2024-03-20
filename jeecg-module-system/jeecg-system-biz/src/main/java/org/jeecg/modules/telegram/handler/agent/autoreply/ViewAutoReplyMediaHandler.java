package org.jeecg.modules.telegram.handler.agent.autoreply;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置自动回复-设置媒体文件-查看"指令
 */
@Slf4j
@Service
public class ViewAutoReplyMediaHandler implements BaseBotMsgHandler {

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String configName = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgAutoReplyConfig config = autoReplyConfigService.getByConfigName(botAgent.getDbBotId(), configName);
        if(StringUtils.isBlank(config.getMediaFile())) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
            answerCallbackQuery.setText("当前未设置媒体文件");  // 设置提示文本
            answerCallbackQuery.setShowAlert(true);  // 设置为true以显示一个警告，而不是一个通知
            botAgent.execute(answerCallbackQuery);
            return;
        }

        BotUtil.sendMediaMsg(botAgent, chatId, config.getMediaType(), config.getMediaFile());
    }

}
