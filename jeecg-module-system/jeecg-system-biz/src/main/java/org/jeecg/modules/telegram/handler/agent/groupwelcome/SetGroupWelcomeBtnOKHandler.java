package org.jeecg.modules.telegram.handler.agent.groupwelcome;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置欢迎语-设置按钮成功"指令
 */
@Slf4j
@Service
public class SetGroupWelcomeBtnOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Resource
    private ITgBotService botService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        TgBot tgBot = botService.getById(botAgent.getDbBotId());

        int total = StringUtils.countMatches(message.getText(), "\n") + StringUtils.countMatches(message.getText(), "|");
        if(tgBot.getServiceType() == 1 && total >= 5) {
            String msg = "⚠️ 免费版最多设置5个消息按钮，请开通专业版！";
            BotUtil.upgradeTips(botAgent, message.getChatId(), msg);
            return;
        }

        Long chatId = message.getChatId();
        String configId = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgWelcomeConfig welcomeConfig = welcomeConfigService.getGroupWelcomeById(configId);
        if(welcomeConfig == null) {
            welcomeConfig = new TgWelcomeConfig();
            welcomeConfig.setBotId(botAgent.getDbBotId());
            welcomeConfig.setMsgButton(message.getText());
            welcomeConfig.setConfigType(2);
            welcomeConfig.setButtonType(1);
            welcomeConfigService.save(welcomeConfig);
        } else {
            welcomeConfig.setMsgButton(message.getText());
            welcomeConfig.setButtonType(1);
            welcomeConfigService.updateById(welcomeConfig);
        }
        String msg = "\uD83C\uDF8A 进群欢迎语设置，关键词：<code>/start</code>。\n" +
                        "\n" +
                        "✅ 设置消息按钮成功";
        MsgContentVO contentVO = welcomeConfigService.groupWelcomeConfigMsg(welcomeConfig);
        botAgent.sendMsgWithData(message.getChatId(), msg, contentVO.getButtonList());
    }

}
