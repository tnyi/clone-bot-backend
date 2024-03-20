package org.jeecg.modules.telegram.handler.agent.welcome;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置欢迎语按钮-删除"指令
 */
@Slf4j
@Service
public class DelWelcomeBtnHandler extends BaseBotDataHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        TgWelcomeConfig welcomeConfig = welcomeConfigService.getBotWelcomeByConfigId(getCallbackData(update));
        if(welcomeConfig != null) {
            welcomeConfig.setMsgButton(null);
            welcomeConfigService.lambdaUpdate()
                                    .set(TgWelcomeConfig::getMsgButton, null)
                                    .eq(TgWelcomeConfig::getId, welcomeConfig.getId()).update();
        }

        String msg = "\uD83C\uDF89 欢迎语设置，关键词：<code>/start</code>。\n" +
                        "\n" +
                        "✅ 消息按钮已经删除";
        MsgContentVO contentVO = welcomeConfigService.welcomeConfigMsg(welcomeConfig);
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
