package org.jeecg.modules.telegram.handler.agent.msgtemplate;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
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
 * 处理代理机器人的"设置消息模板-添加消息模板-成功"指令
 */
@Slf4j
@Service
public class AddMsgTemplateOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgMsgTemplateService msgTemplateService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        String botId = botAgent.getDbBotId();
        String configName = message.getText();
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        TgMsgTemplate config = msgTemplateService.getByConfigName(botId, configName);
        if(config == null) {
            config = new TgMsgTemplate();
            config.setBotId(botId);
            config.setTemplateName(configName);
            msgTemplateService.save(config);
        }
        else {
            botAgent.sendMsg(message.getChatId(), "该名字已经添加");
            return;
        }
        String msg = "✅ 添加消息模板成功：" + configName;
        MsgContentVO contentVO = msgTemplateService.tepmlateConfigMsg(botId);
        botAgent.sendMsgWithData(message.getChatId(), msg, contentVO.getButtonList());
    }

}
