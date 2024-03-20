package org.jeecg.modules.telegram.handler.agent.groupwelcome;

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
 * 处理代理机器人的"设置进群欢迎语-删除"指令
 */
@Slf4j
@Service
public class DelGroupWelcomeHandler extends BaseBotDataHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        TgWelcomeConfig welcomeConfig = welcomeConfigService.getGroupWelcomeById(getCallbackData(update));
        if(welcomeConfig != null) {
            welcomeConfigService.removeById(welcomeConfig);
        }

        String msg = "✅ 删除进群欢迎语成功：<b>" + welcomeConfig.getConfigName() + "</b>";
        MsgContentVO contentVO = welcomeConfigService.groupWelcomeConfigListMsg(botAgent.getDbBotId());
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
