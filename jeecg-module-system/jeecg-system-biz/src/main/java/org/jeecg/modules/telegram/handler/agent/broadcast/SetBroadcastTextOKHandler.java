package org.jeecg.modules.telegram.handler.agent.broadcast;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 处理代理机器人的"广播-设置成功"指令
 */
@Slf4j
@Service
public class SetBroadcastTextOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgChatService chatService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        List<TgChat> chatList = chatService.lambdaQuery().eq(TgChat::getBotId, botAgent.getDbBotId()).list();
        BigDecimal costTime = RandomUtil.randomBigDecimal(new BigDecimal(1), new BigDecimal(2)).setScale(2, BigDecimal.ROUND_HALF_UP);
        String msg = "\uD83D\uDCE1 当前需要广播群数："+chatList.size()+"，预计用时 "+costTime+" 秒请点击 <b>开始</b> 发送广播。\n" +
                        "\n" +
                        "<i>广播进行过程中，请勿删除这条需要广播的消息！</i>";

        InlineButtonVO.InlineButtonBuilder butBuilder = InlineButtonVO.builder();
        butBuilder = butBuilder.addRow()
                .addDataButton("▶️开始", message.getText(), SendBroadcastHandler.class)
                .addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class);
        botAgent.sendMsgWithData(message.getChatId(), msg, butBuilder.build(), message.getMessageId());
    }

}
