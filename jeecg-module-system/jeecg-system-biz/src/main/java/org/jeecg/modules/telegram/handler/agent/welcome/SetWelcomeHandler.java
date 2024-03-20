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
 * 处理代理机器人的"开始"指令
 */
@Slf4j
@Service
public class SetWelcomeHandler extends BaseBotDataHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        String configId = getCallbackData(update);
        TgWelcomeConfig welcomeConfig = welcomeConfigService.getById(configId);
        String msg = "\uD83C\uDF89 <b>"+welcomeConfig.getConfigName()+"</b> 设置，关键词：/start\n" +
                        "\n" +
                        "\uD83D\uDCAC 温馨提示：\n" +
                        "一条欢迎语不能同时设置 <b>底部按钮</b> 或者 <b>消息按钮</b>，\n" +
                        "如果需要当用户点击 /start 同时获得2种按钮，可以设置2条欢迎语，\n" +
                        "第一条设置 <b>底部按钮</b>，第二条设置 <b>消息按钮</b>。";
        Message message = update.getCallbackQuery().getMessage();
        MsgContentVO contentVO = welcomeConfigService.welcomeConfigMsg(configId);
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
