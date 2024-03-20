package org.jeecg.modules.telegram.handler.agent.blacklist;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.service.ITgBlacklistService;
import org.jeecg.modules.telegram.vo.DelBlacklistVO;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"黑名单-删除"指令
 */
@Slf4j
@Service
public class DelBlacklistHandler extends BaseBotDataHandler {

    @Resource
    private ITgBlacklistService blacklistService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = getCallbackData(update);
        DelBlacklistVO delVO = JSON.parseObject(data, DelBlacklistVO.class);
        TgBlacklist config = blacklistService.getByUserId(botAgent.getDbBotId(), delVO.getUserId());
        if(config != null) {
            blacklistService.removeById(config);
        }

        String msg = "✅ 删除黑名单成功：" + delVO.getUserId();
        MsgContentVO contentVO = blacklistService.blacklistConfigMsg(botAgent.getDbBotId(), delVO.getPageNo(), delVO.getPageSize());
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
