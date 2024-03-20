package org.jeecg.modules.telegram.handler.agent.stat;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.service.ITgBlacklistService;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 处理代理机器人的"统计"指令
 */
@Slf4j
@Service
public class ShowStatHandler extends BaseBotDataHandler {

    @Resource
    private ITgChatService chatService;

    @Resource
    private ITgBlacklistService blacklistService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        List<TgBlacklist> blacklists = blacklistService.lambdaQuery().eq(TgBlacklist::getBotId, botAgent.getDbBotId()).list();
        Message message = update.getCallbackQuery().getMessage();

        long total = chatService.lambdaQuery().eq(TgChat::getBotId, botAgent.getDbBotId()).count();
        Date now = new Date();
        Date beginTime = DateUtil.beginOfDay(now);
        Date endTime = DateUtil.endOfDay(now);
        long newCount = chatService.lambdaQuery().eq(TgChat::getBotId, botAgent.getDbBotId()).between(TgChat::getCreateTime, beginTime, endTime).count();

        String msg = "\uD83D\uDCCA 统计\n" +
                        "\n" +
                        "总用户数：" + total + "\n" +
                        "当日新增用户：" + newCount + "\n" +
                        "已拉黑数量：" + blacklists.size();
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder()
                .addRow()
                .addDataButton("返回", BackToMainHandler.class);

        callbackDataService.del(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + message.getChatId());
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }


}
