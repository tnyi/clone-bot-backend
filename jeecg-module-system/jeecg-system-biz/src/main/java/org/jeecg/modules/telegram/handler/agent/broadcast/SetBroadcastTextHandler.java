package org.jeecg.modules.telegram.handler.agent.broadcast;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.handler.agent.HtmlTagHelpHandler;
import org.jeecg.modules.telegram.handler.agent.welcome.SetWelcomeTextOKHandler;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"广播"指令
 */
@Slf4j
@Service
public class SetBroadcastTextHandler implements BaseBotMsgHandler {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        BotUtil.setTxtRelyHandler(botAgent.getDbBotId(),  message.getChatId(), SetBroadcastTextOKHandler.class);
        TgBot tgBot = botService.getById(botAgent.getDbBotId());

        String prefix = BotRedisConstant.BROADCAST_STAT + message.getChatId() + ":" + DateUtils.formatDate();
        Integer broadcastCount = (Integer)redisUtil.get(prefix);
        broadcastCount = (broadcastCount==null) ? 0 : broadcastCount;
        redisUtil.set(prefix, broadcastCount, 86400);
        if(tgBot.getServiceType() == 1 && broadcastCount >= 1) {
            String msg = "⚠️ 免费版每天最多进行1次广播，请开通专业版！";
            BotUtil.upgradeTips(botAgent, message.getChatId(), msg);
            return;
        }

        String msg = "\uD83D\uDC49 请回复或者转发需要广播的内容，也可回复任一消息模板（通过 . 或者 / 触发）：";
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        btnBuilder
                .addRow()
                .addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class)
                .build();
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }

}
