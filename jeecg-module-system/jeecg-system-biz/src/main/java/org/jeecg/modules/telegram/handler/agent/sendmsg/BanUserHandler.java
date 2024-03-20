package org.jeecg.modules.telegram.handler.agent.sendmsg;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.agent.blacklist.AddBlacklistHandler;
import org.jeecg.modules.telegram.handler.agent.blacklist.UnlockUserHandler;
import org.jeecg.modules.telegram.service.ITgBlacklistService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * 拉黑发送消息的用户
 */
@Slf4j
@Service
public class BanUserHandler extends BaseBotDataHandler {

    @Resource
    private ITgBlacklistService blacklistService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();

        String dataMapStr = getCallbackData(update);
        Map<String, String> dataMap = JSON.parseObject(dataMapStr, Map.class);
        String userId = dataMap.get("userId");
        String banChatId = dataMap.get("chatId");
        String username = dataMap.get("username");
        String srcType = dataMap.get("srcType");
        Integer banType = Integer.parseInt(dataMap.get("banType"));
        Date now = new Date();
        Date end = null;
        String endStr = "永久";
        if(banType == 1) {
            end = DateUtil.offsetDay(now, 1);
            endStr = DateUtil.formatDateTime(end);
        } else if(banType == 2) {
            end = DateUtil.offsetWeek(now, 1);
            endStr = DateUtil.formatDateTime(end);
        } else if(banType == 3) {
            end = DateUtil.offsetMonth(now, 1);
            endStr = DateUtil.formatDateTime(end);
        } else {
            end = DateUtil.offsetMonth(now, 120);
        }

        TgBlacklist blacklist = blacklistService.lambdaQuery().eq(TgBlacklist::getBotId, botAgent.getDbBotId()).eq(TgBlacklist::getTgUserId, userId).one();
        if(blacklist == null) {
            blacklist = new TgBlacklist();
            blacklist.setTgUserId(userId);
            blacklist.setBotId(botAgent.getDbBotId());
            blacklist.setBanType(banType);
            blacklist.setBanEndTime(now);
            blacklist.setBanEndTime(end);
            blacklist.setChatId(banChatId);
            blacklist.setTgUsername(username);
            blacklistService.save(blacklist);
        } else {
            blacklist.setBanType(banType);
            blacklist.setBanEndTime(now);
            blacklist.setBanEndTime(end);
            blacklistService.updateById(blacklist);
        }

        String tips = "已将 <b><a href=\"tg://user?id=" + dataMap.get("userId") + "\">" + dataMap.get("username") + "</a></b> 拉黑，解封时间：" + endStr;
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        if("start".equals(srcType)) {
            btnBuilder.addRow().addDataButton("\uD83D\uDFE2解封：" + dataMap.get("username"), dataMapStr, UnbanUserHandler.class);
        } else {
            btnBuilder.addRow()
                    .addDataButton("\uD83D\uDEB7拉黑用户", AddBlacklistHandler.class)
                    .addDataButton("\uD83D\uDFE2解封用户", UnlockUserHandler.class);
        }
        botAgent.sendEditMsg(chatId, tips, btnBuilder.build(), message.getMessageId());
    }

}
