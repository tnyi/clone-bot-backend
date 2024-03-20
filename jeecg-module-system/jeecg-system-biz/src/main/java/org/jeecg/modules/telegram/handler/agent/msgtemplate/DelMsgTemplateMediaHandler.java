package org.jeecg.modules.telegram.handler.agent.msgtemplate;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgMsgTemplate;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgMsgTemplateService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置消息模板-媒体文件-删除"指令
 */
@Slf4j
@Service
public class DelMsgTemplateMediaHandler implements BaseBotMsgHandler {

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgMsgTemplateService msgTemplateService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String configName = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgMsgTemplate replyConfig = msgTemplateService.getByConfigName(botAgent.getDbBotId(), configName);
        String oldFilePath = replyConfig.getMediaFile();
        replyConfig.setMediaFile(null);
        msgTemplateService.lambdaUpdate().set(TgMsgTemplate::getMediaFile, null).eq(TgMsgTemplate::getId, replyConfig.getId()).update();
        java.io.File file = new java.io.File(uploadpath + oldFilePath);
        if(file.exists()) {
            file.delete(); //删除旧文件
        }

        String msg = BotUtil.msgTemplateTips(replyConfig) +
                        "\n" +
                        "✅ 媒体文件已经删除";
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        MsgContentVO contentVO = msgTemplateService.configItemMsg(replyConfig);
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
