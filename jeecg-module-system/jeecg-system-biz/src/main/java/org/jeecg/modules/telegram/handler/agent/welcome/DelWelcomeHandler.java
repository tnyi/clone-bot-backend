package org.jeecg.modules.telegram.handler.agent.welcome;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置欢迎语-删除"指令
 */
@Slf4j
@Service
public class DelWelcomeHandler extends BaseBotDataHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        long welcomeCount = welcomeConfigService.lambdaQuery()
                                                .eq(TgWelcomeConfig::getBotId, botAgent.getDbBotId())
                                                .eq(TgWelcomeConfig::getConfigType, 1).count();
        if(welcomeCount == 1) {
            //必须保留一条欢迎语
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
            answerCallbackQuery.setText("⚠️不能删除唯一的欢迎语");
            answerCallbackQuery.setShowAlert(true);  // 设置为true以显示一个警告，而不是一个通知
            botAgent.execute(answerCallbackQuery);
            return;
        }
        TgWelcomeConfig welcomeConfig = welcomeConfigService.getBotWelcomeByConfigId(getCallbackData(update));
        if(welcomeConfig != null) {
            welcomeConfigService.removeById(welcomeConfig);
        }

        String msg = "✅ 删除欢迎语成功：<b>" + welcomeConfig.getConfigName() + "</b>";
        MsgContentVO contentVO = welcomeConfigService.welcomeConfigListMsg(botAgent.getDbBotId());
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
