package org.jeecg.modules.telegram.handler.agent.msgtemplate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgMsgTemplate;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgMsgTemplateService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置欢迎语-预览"指令
 */
@Slf4j
@Service
public class PreviewMsgTemplateHandler implements BaseBotMsgHandler {

    @Resource
    private ITgMsgTemplateService msgTemplateService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String configName = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgMsgTemplate config = msgTemplateService.getByConfigName(botAgent.getDbBotId(), configName);

        String txt = config != null ? config.getTextMsg() : null;
        if(config == null || StringUtils.isBlank(txt)) {
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
