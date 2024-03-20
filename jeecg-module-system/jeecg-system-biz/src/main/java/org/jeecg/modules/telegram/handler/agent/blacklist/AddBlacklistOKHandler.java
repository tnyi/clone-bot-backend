package org.jeecg.modules.telegram.handler.agent.blacklist;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotBizConstant;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.service.ITgBlacklistService;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 处理代理机器人的"黑名单-拉黑用户-成功"指令
 */
@Slf4j
@Service
public class AddBlacklistOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBlacklistService blacklistService;

    @Resource
    private ITgChatService chatService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        String botId = botAgent.getDbBotId();
        String fromUserId = String.valueOf(message.getFrom().getId());
        String tgUserId = message.getText();

        ArrayList<List<InlineButtonVO>> butList = InlineButtonVO.builder().addRow().addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class).build();
        if(fromUserId.equals(tgUserId)) {
            botAgent.sendMsgWithData(message.getChatId(), "您不能拉黑自己", butList);
            return;
        }

        TgChat userChat = chatService.lambdaQuery().eq(TgChat::getBotId, botId).eq(TgChat::getAdminId, tgUserId).eq(TgChat::getChatType, 1).last("limit 1").one();
        if(userChat == null) {
            botAgent.sendMsgWithData(message.getChatId(), "用户id错误，请检查后重新回复", butList);
            return;
        }

        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        TgBlacklist config = blacklistService.getByUserId(botId, tgUserId);
        if(config == null) {
            config = new TgBlacklist();
            config.setBotId(botId);
            config.setTgUserId(tgUserId);
            config.setBanType(4);
            config.setBanBeginTime(new Date());
            config.setBanEndTime(new Date(System.currentTimeMillis() + BotBizConstant.PERMANENT_EXPIRE_TIME));
            config.setTgUsername(userChat.getAdminUsername());
            config.setChatId(userChat.getChatId());
            blacklistService.save(config);
        }
        else {
            botAgent.sendMsgWithData(message.getChatId(), "该用户ID已经添加", butList);
            return;
        }
        String msg = "✅ 添加黑名单成功：" + tgUserId;
        MsgContentVO contentVO = blacklistService.blacklistConfigMsg(botId, 1, 10);
        botAgent.sendMsgWithData(message.getChatId(), msg, contentVO.getButtonList());
    }

}
