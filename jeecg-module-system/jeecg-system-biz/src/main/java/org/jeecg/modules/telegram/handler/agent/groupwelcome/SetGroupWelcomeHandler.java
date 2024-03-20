package org.jeecg.modules.telegram.handler.agent.groupwelcome;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
 * 处理代理机器人的"进群欢迎语-设置具体项"指令
 */
@Slf4j
@Service
public class SetGroupWelcomeHandler extends BaseBotDataHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        String configId = getCallbackData(update);
        TgWelcomeConfig welcomeConfig = welcomeConfigService.getById(configId);
        String msg = "\uD83C\uDF8A <b>"+welcomeConfig.getConfigName()+"</b> 设置\n" +
                            "\n" +
                            "注：进群欢迎语需要在个性化设置中开启，给机器人管理员权限\n\n";
        if(StringUtils.isBlank(welcomeConfig.getTextMsg())) {
            msg = msg + "⚠️ 该进群欢迎未生效，原因如下：\n" +
                        "\uD83D\uDC49 未设置 <b>回复文本</b> 或者 <b>媒体文件</b>";
        };
        Message message = update.getCallbackQuery().getMessage();
        MsgContentVO contentVO = welcomeConfigService.groupWelcomeConfigMsg(configId);
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
