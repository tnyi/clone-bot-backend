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
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置消息模板-某条目-设置文本-成功"指令
 */
@Slf4j
@Service
public class SetMsgTemplateTextOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgMsgTemplateService msgTemplateService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String configName = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgMsgTemplate replyConfig = msgTemplateService.getByConfigName(botAgent.getDbBotId(), configName);
        replyConfig.setTextMsg(message.getText());
        msgTemplateService.updateById(replyConfig);

        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        String msg = BotUtil.msgTemplateTips(replyConfig) +
                        "\n" +
                        "✅ 设置回复文本成功";
        MsgContentVO contentVO = msgTemplateService.configItemMsg(replyConfig);
        botAgent.sendMsgWithData(chatId, msg, contentVO.getButtonList());
    }

}
