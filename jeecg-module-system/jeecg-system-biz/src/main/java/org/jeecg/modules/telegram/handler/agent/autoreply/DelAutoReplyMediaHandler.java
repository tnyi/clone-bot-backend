package org.jeecg.modules.telegram.handler.agent.autoreply;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置自动回复-媒体文件-删除"指令
 */
@Slf4j
@Service
public class DelAutoReplyMediaHandler extends BaseBotDataHandler {

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String configName = getCallbackData(update);
        TgAutoReplyConfig replyConfig = autoReplyConfigService.getByConfigName(botAgent.getDbBotId(), configName);
        String oldFilePath = replyConfig.getMediaFile();
        replyConfig.setMediaFile(null);
        autoReplyConfigService.lambdaUpdate().set(TgAutoReplyConfig::getMediaFile, null).eq(TgAutoReplyConfig::getId, replyConfig.getId()).update();
        java.io.File file = new java.io.File(uploadpath + oldFilePath);
        if(file.exists()) {
            file.delete(); //删除旧文件
        }

        String keywordTips = BotUtil.autoReplyKeywordTips(replyConfig);
        String msg = "\uD83C\uDF89 欢迎语设置，"+keywordTips+"\n" +
                        "\n" +
                        "✅ 媒体文件已经删除";
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        MsgContentVO contentVO = autoReplyConfigService.configItemMsg(replyConfig);
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
