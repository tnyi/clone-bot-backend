package org.jeecg.modules.telegram.handler.agent.groupwelcome;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置欢迎语-设置媒体文件-查看"指令
 */
@Slf4j
@Service
public class ViewGroupWelcomeMediaHandler implements BaseBotMsgHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        TgWelcomeConfig config = welcomeConfigService.getGroupWelcomeById(botAgent.getDbBotId());
        if(config == null || StringUtils.isBlank(config.getMediaFile())) {
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
