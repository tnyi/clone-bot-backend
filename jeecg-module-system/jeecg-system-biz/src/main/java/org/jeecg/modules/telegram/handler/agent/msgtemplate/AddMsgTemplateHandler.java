package org.jeecg.modules.telegram.handler.agent.msgtemplate;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgMsgTemplate;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgMsgTemplateService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置消息模板-添加消息模板"指令
 */
@Slf4j
@Service
public class AddMsgTemplateHandler implements BaseBotMsgHandler {

    @Resource
    private ITgMsgTemplateService msgTemplateService;

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        TgBot tgBot = botService.getById(botAgent.getDbBotId());
        long count = msgTemplateService.lambdaQuery().eq(TgMsgTemplate::getBotId, botAgent.getDbBotId()).count();
        if(tgBot.getServiceType() == 1 && count >= 5) {
            String msg = "⚠️ 免费版最多设置5条消息模板，请开通专业版！";
            BotUtil.upgradeTips(botAgent, message.getChatId(), msg);
            return;
        }

        BotUtil.setTxtRelyHandler(botAgent.getDbBotId(),  message.getChatId(), AddMsgTemplateOKHandler.class);
        String msg = "\uD83D\uDC49 请设置消息模板的名字，可通过 /<b>名字</b> 或者 .<b>名字</b> 触发消息模板：";
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        btnBuilder
                .addRow()
                .addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class)
                .build();
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }

}
