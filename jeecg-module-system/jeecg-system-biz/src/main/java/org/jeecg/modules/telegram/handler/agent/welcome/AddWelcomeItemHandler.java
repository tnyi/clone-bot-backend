package org.jeecg.modules.telegram.handler.agent.welcome;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotBizConstant;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置欢迎语-添加欢迎语"指令
 */
@Slf4j
@Service
public class AddWelcomeItemHandler implements BaseBotMsgHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        TgWelcomeConfig welcomeConfig = welcomeConfigService.lambdaQuery()
                                                    .eq(TgWelcomeConfig::getBotId, botAgent.getDbBotId())
                                                    .eq(TgWelcomeConfig::getConfigType, 1)
                                                    .orderByDesc(TgWelcomeConfig::getCreateTime)
                                                    .last("limit 1").one();
        int startIndex = 1;
        if(welcomeConfig != null && !"/start".equals(welcomeConfig.getConfigName())) {
            int pos = welcomeConfig.getConfigName().lastIndexOf("_");
            if(pos == -1) {
                startIndex = 1;
            } else {
                String indexStr = welcomeConfig.getConfigName().substring(pos+1);
                startIndex = Integer.parseInt(indexStr) + 1;
            }
        }
        TgWelcomeConfig newConfig = new TgWelcomeConfig();
        newConfig.setConfigName("欢迎语_" + startIndex);
        newConfig.setTextMsg(BotBizConstant.DEFAULT_WELCOME_TEXT);
        newConfig.setConfigType(1);
        newConfig.setBotId(botAgent.getDbBotId());
        welcomeConfigService.save(newConfig);

        String msg = "✅ 添加欢迎语成功";
        Message message = update.getCallbackQuery().getMessage();
        MsgContentVO contentVO = welcomeConfigService.welcomeConfigListMsg(botAgent.getDbBotId());
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
