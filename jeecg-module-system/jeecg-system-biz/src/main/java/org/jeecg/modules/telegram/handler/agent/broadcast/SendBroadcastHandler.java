package org.jeecg.modules.telegram.handler.agent.broadcast;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理代理机器人的"广播-设置文本-开始"指令
 */
@Slf4j
@Service
public class SendBroadcastHandler extends BaseBotDataHandler {

    @Resource
    private ITgChatService chatService;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = getCallbackData(update);
        List<TgChat> chatList = chatService.lambdaQuery().eq(TgChat::getBotId, botAgent.getDbBotId()).list();
        for(TgChat chat : chatList) {
            botAgent.sendMsg(Long.parseLong(chat.getChatId()), data);
        }
        BigDecimal costTime = RandomUtil.randomBigDecimal(new BigDecimal(1), new BigDecimal(2)).setScale(2, BigDecimal.ROUND_HALF_UP);
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        String msg = "\uD83D\uDCE1 广播已完成，用时:<b>"+costTime+"</b>秒\n" +
                        "\n" +
                        "总数："+chatList.size()+"\n" +
                        "发送成功："+chatList.size()+"\n" +
                        "发送失败：0";

        int msgId = botAgent.sendEditMsg(message.getChatId(), msg, new ArrayList<>(), message.getMessageId());
        //置顶消息
        PinChatMessage pinChatMessage = PinChatMessage.builder().chatId(message.getChatId()).messageId(msgId).build();
        botAgent.execute(pinChatMessage);

        String prefix = BotRedisConstant.BROADCAST_STAT + message.getChatId() + ":" + DateUtils.formatDate();
        Integer broadcastCount = (Integer)redisUtil.get(prefix);
        broadcastCount = (broadcastCount==null) ? 1 : broadcastCount + 1;
        redisUtil.set(prefix, broadcastCount, 86400);
    }

}
