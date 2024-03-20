package org.jeecg.modules.telegram.handler.agent.groupwelcome;

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
 * 处理代理机器人的"设置进群欢迎语-设置欢迎语保留时间-成功"指令
 */
@Slf4j
@Service
public class SetGroupWelcomeSaveTimeOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String txt = message.getText();
        if (!txt.matches("[0-9]+") || txt.startsWith("0")) {
            String msg = "请输入正整数！";
            botAgent.sendMsg(chatId, msg);
            return;
        }

        int saveTime = Integer.parseInt(txt);
        String configId = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgWelcomeConfig welcomeConfig = welcomeConfigService.getGroupWelcomeById(configId);
        if(welcomeConfig != null) {
            welcomeConfig.setSaveTime(saveTime);
            welcomeConfigService.updateById(welcomeConfig);
        }

        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        String msg = "\uD83C\uDF8A <b>"+welcomeConfig.getConfigName()+"</b>设置\n" +
                        "\n" +
                        "✅ 设置欢迎语保留时间成功";
        MsgContentVO contentVO = welcomeConfigService.groupWelcomeConfigListMsg(botAgent.getDbBotId());
        botAgent.sendMsgWithData(message.getChatId(), msg, contentVO.getButtonList());
    }

}
