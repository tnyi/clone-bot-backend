package org.jeecg.modules.telegram.handler.agent.msgtemplate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.service.ITgBotService;
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
 * 处理代理机器人的"设置消息模板-设置按钮成功"指令
 */
@Slf4j
@Service
public class SetMsgTemplateBtnOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgMsgTemplateService msgTemplateService;

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        TgBot tgBot = botService.getById(botAgent.getDbBotId());

        int total = StringUtils.countMatches(message.getText(), "\n") + StringUtils.countMatches(message.getText(), "|");
        if(tgBot.getServiceType() == 1 && total >= 5) {
            String msg = "⚠️ 免费版最多设置5个消息按钮，请开通专业版！";
            BotUtil.upgradeTips(botAgent, message.getChatId(), msg);
            return;
        }

        String configName = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgMsgTemplate replyConfig = msgTemplateService.getByConfigName(botAgent.getDbBotId(), configName);
        replyConfig.setMsgButton(message.getText());
        msgTemplateService.updateById(replyConfig);

        String msg = BotUtil.msgTemplateTips(replyConfig) +
                        "\n" +
                        "✅ 设置消息按钮成功";
        MsgContentVO contentVO = msgTemplateService.configItemMsg(replyConfig);
        botAgent.sendMsgWithData(chatId, msg, contentVO.getButtonList());
    }

}
