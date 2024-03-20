package org.jeecg.modules.telegram.handler.agent.groupwelcome;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.constant.BotBizConstant;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置欢迎语-设置文本-查看"指令
 */
@Slf4j
@Service
public class ViewGroupWelcomeTextHandler extends BaseBotDataHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        TgWelcomeConfig tgWelcomeConfig = welcomeConfigService.getGroupWelcomeById(getCallbackData(update));
        String txt = tgWelcomeConfig != null ? tgWelcomeConfig.getTextMsg() : null;
        String msg = StringUtils.isBlank(txt) ? BotBizConstant.DEFAULT_WELCOME_TEXT : txt;
        Message message = update.getCallbackQuery().getMessage();
        botAgent.sendMsg(message.getChatId(), msg);
    }

}
