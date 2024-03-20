package org.jeecg.modules.telegram.handler.agent.groupwelcome;

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
 * 处理代理机器人的"设置进群欢迎语"指令
 */
@Slf4j
@Service
public class GroupWelcomeListHandler implements BaseBotMsgHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "\uD83C\uDF8A 点击按钮，进行相应进群欢迎的设置。\n" +
                        "\n" +
                        "\uD83D\uDCAC 温馨提示：\n" +
                        "\n" +
                        "\uD83D\uDC49 中间列为图标显示，具体含义点击可以查看。⚠️表示未生效。\n" +
                        "\n" +
                        "<i>注：<b>进群欢迎语</b>需要在个性化设置中开启，给机器人管理员权限</i>";
        Message message = update.getCallbackQuery().getMessage();
        MsgContentVO contentVO = welcomeConfigService.groupWelcomeConfigListMsg(botAgent.getDbBotId());
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
