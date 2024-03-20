package org.jeecg.modules.telegram.handler.agent.welcome;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置欢迎语"指令
 */
@Slf4j
@Service
public class WelcomeListHandler implements BaseBotMsgHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "\uD83C\uDF89 点击按钮，进行相应欢迎语的设置。\n" +
                        "\n" +
                        "\uD83D\uDCAC 温馨提示：\n" +
                        "\n" +
                        "\uD83D\uDC49 中间列为图标显示，具体含义点击可以查看。⚠️表示未生效。\n" +
                        "\n" +
                        "\uD83D\uDC49 当用户发送 /start 时，多条欢迎语会按顺序同时触发";
        Message message = update.getCallbackQuery().getMessage();
        MsgContentVO contentVO = welcomeConfigService.welcomeConfigListMsg(botAgent.getDbBotId());
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
