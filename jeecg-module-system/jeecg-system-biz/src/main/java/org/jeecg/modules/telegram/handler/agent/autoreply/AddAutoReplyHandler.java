package org.jeecg.modules.telegram.handler.agent.autoreply;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置自动回复-添加自动回复"指令
 */
@Slf4j
@Service
public class AddAutoReplyHandler implements BaseBotMsgHandler {

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        long existCount = autoReplyConfigService.lambdaQuery().eq(TgAutoReplyConfig::getBotId, botAgent.getDbBotId()).count();

        TgBot tgBot = botService.getById(botAgent.getDbBotId());
        if(tgBot.getServiceType() == 1 && existCount >= 5) {
            String msg = "⚠️ 免费版最多设置5条自动回复，请开通专业版！";
            BotUtil.upgradeTips(botAgent, message.getChatId(), msg);
            return;
        }

        BotUtil.setTxtRelyHandler(botAgent.getDbBotId(),  message.getChatId(), AddAutoReplyOKHandler.class);
        String msg = "\uD83D\uDC49 请为这条自动回复取一个响亮的名字：";
        botAgent.sendMsg(message.getChatId(), msg);
    }

}
