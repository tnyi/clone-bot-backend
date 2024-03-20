package org.jeecg.modules.telegram.handler.agent.sendmsg;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.agent.ViewEmptyBtnHandler;
import org.jeecg.modules.telegram.handler.agent.blacklist.AddBlacklistHandler;
import org.jeecg.modules.telegram.handler.agent.blacklist.UnlockUserHandler;
import org.jeecg.modules.telegram.service.ITgBlacklistService;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 解封被拉黑的用户
 */
@Slf4j
@Service
public class UnbanUserHandler extends BaseBotDataHandler {

    @Resource
    private ITgBotService tgBotService;

    @Resource
    private ITgBlacklistService blacklistService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        TgBot tgBot = tgBotService.getById(botAgent.getDbBotId());
        String dataMapStr = getCallbackData(update);

        Map<String, String> dataMap = JSON.parseObject(dataMapStr, Map.class);
        String username = dataMap.get("username");
        String userId = dataMap.get("userId");
        String srcType = dataMap.get("srcType");
        TgBlacklist blacklist = blacklistService.lambdaQuery().eq(TgBlacklist::getBotId, botAgent.getDbBotId()).eq(TgBlacklist::getTgUserId, userId).one();
        if(blacklist != null) {
            blacklistService.removeById(blacklist);
        }

        LinkedList<String> timeArr = new LinkedList<>();
        for(int i=1; i<=4; i++) {
            dataMap.put("banType", String.valueOf(i));
            String banMapStr = JSON.toJSONString(dataMap);
            timeArr.add(banMapStr);
        }

        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        String tips = "<b><a href=\"tg://user?id=" + dataMap.get("userId") + "\">" + dataMap.get("username") + "</a></b> 已解封";
        if("start".equals(srcType)) {
            btnBuilder
                    .addRow()
                    .addDataButton("\uD83D\uDD04切换至：" + username, dataMapStr, SwitchRecvUserHandler.class)
                    .addRow()
                    .addDataButton("\uD83D\uDEB71天", timeArr.get(0), BanUserHandler.class)
                    .addDataButton("\uD83D\uDEB71周", timeArr.get(1), BanUserHandler.class)
                    .addDataButton("\uD83D\uDEB71月", timeArr.get(2), BanUserHandler.class)
                    .addDataButton("\uD83D\uDEB7永久", timeArr.get(3), BanUserHandler.class)
                    .addRow()
                    .addDataButton("\uD83D\uDCAC" + userId, dataMapStr, ViewUserInfoHandler.class)
                    .addDataButton("\uD83E\uDD16Bot已自动回复" + tgBot.getReplyCount() + "次", ViewEmptyBtnHandler.class);
        }
        else {
            btnBuilder.addRow()
                    .addDataButton("\uD83D\uDEB7拉黑用户", AddBlacklistHandler.class)
                    .addDataButton("\uD83D\uDFE2解封用户", UnlockUserHandler.class);
        }
        botAgent.sendEditMsg(chatId, tips, btnBuilder.build(), message.getMessageId());
    }

}
