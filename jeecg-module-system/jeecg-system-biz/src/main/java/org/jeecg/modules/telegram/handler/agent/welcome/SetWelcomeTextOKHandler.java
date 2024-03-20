package org.jeecg.modules.telegram.handler.agent.welcome;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置欢迎语文本成功"指令
 */
@Slf4j
@Service
public class SetWelcomeTextOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String configId = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgWelcomeConfig welcomeConfig = welcomeConfigService.getBotWelcomeByConfigId(configId);
        if(welcomeConfig == null) {
            welcomeConfig = new TgWelcomeConfig();
            welcomeConfig.setBotId(botAgent.getDbBotId());
            welcomeConfig.setTextMsg(message.getText());
            welcomeConfig.setConfigType(1);
            welcomeConfigService.save(welcomeConfig);
        } else {
            welcomeConfig.setTextMsg(message.getText());
            welcomeConfigService.updateById(welcomeConfig);
        }

        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        String msg = "\uD83C\uDF89 欢迎语设置，关键词：<code>/start</code>。\n" +
                        "\n" +
                        "✅ 设置回复文本成功";
        MsgContentVO contentVO = welcomeConfigService.welcomeConfigMsg(welcomeConfig);
        botAgent.sendMsgWithData(message.getChatId(), msg, contentVO.getButtonList());
    }

}
