package org.jeecg.modules.telegram.handler.agent.welcome;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置欢迎语-媒体文件-删除"指令
 */
@Slf4j
@Service
public class DelWelcomeMediaHandler extends BaseBotDataHandler {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        TgWelcomeConfig welcomeConfig = welcomeConfigService.getBotWelcomeByConfigId(getCallbackData(update));
        if(welcomeConfig != null) {
            String oldFilePath = welcomeConfig.getMediaFile();
            welcomeConfig.setMediaFile(null);
            welcomeConfigService.lambdaUpdate()
                                    .set(TgWelcomeConfig::getMediaFile, null)
                                    .eq(TgWelcomeConfig::getId, welcomeConfig.getId())
                                    .update();
            java.io.File file = new java.io.File(uploadpath + oldFilePath);
            if(file.exists()) {
                file.delete(); //删除旧文件
            }
        }

        String msg = "\uD83C\uDF89 欢迎语设置，关键词：<code>/start</code>。\n" +
                        "\n" +
                        "✅ 媒体文件已经删除";
        MsgContentVO contentVO = welcomeConfigService.welcomeConfigMsg(welcomeConfig);
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
