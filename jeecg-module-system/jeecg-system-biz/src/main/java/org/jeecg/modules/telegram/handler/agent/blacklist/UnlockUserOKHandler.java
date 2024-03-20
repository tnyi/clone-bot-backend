package org.jeecg.modules.telegram.handler.agent.blacklist;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgBlacklistService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"黑名单-拉黑用户-成功"指令
 */
@Slf4j
@Service
public class UnlockUserOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBlacklistService blacklistService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        String botId = botAgent.getDbBotId();
        String tgUserId = message.getText();
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        TgBlacklist config = blacklistService.getByUserId(botId, tgUserId);
        if(config != null) {
            blacklistService.removeById(config);
        }
        else {
            botAgent.sendMsg(message.getChatId(), "该用户ID不在黑名单");
            return;
        }
        String msg = "✅ 解封成功：" + tgUserId;
        MsgContentVO contentVO = blacklistService.blacklistConfigMsg(botId, 1, 10);
        botAgent.sendMsgWithData(message.getChatId(), msg, contentVO.getButtonList());
    }

}
