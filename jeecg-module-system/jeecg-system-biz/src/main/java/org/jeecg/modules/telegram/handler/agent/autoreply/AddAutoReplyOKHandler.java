package org.jeecg.modules.telegram.handler.agent.autoreply;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置自动回复-添加自动回复-成功"指令
 */
@Slf4j
@Service
public class AddAutoReplyOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        String botId = botAgent.getDbBotId();
        String configName = message.getText();
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        TgAutoReplyConfig config = autoReplyConfigService.getByConfigName(botId, configName);
        if(config == null) {
            config = new TgAutoReplyConfig();
            config.setBotId(botId);
            config.setConfigName(configName);
            autoReplyConfigService.save(config);
        }
        else {
            botAgent.sendMsg(message.getChatId(), "该名字已经添加");
            return;
        }
        String msg = "✅ 添加自动回复成功：" + configName;
        MsgContentVO contentVO = autoReplyConfigService.autoReplyConfigMsg(botId);
        botAgent.sendMsgWithData(message.getChatId(), msg, contentVO.getButtonList());
    }

}
