package org.jeecg.modules.telegram.handler.agent.autoreply;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置自动回复-某条记录-开始设置"指令
 */
@Slf4j
@Service
public class ConfigAutoReplyItemHandler extends BaseBotDataHandler {

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String configName = getCallbackData(update);
        TgAutoReplyConfig replyConfig = autoReplyConfigService.getByConfigName(botAgent.getDbBotId(), configName);

        String keywordTips = BotUtil.autoReplyKeywordTips(replyConfig);
        String msg = "\uD83E\uDD16 自动回复 "+configName+" 设置，" + keywordTips;
        MsgContentVO contentVO = autoReplyConfigService.configItemMsg(replyConfig);
        botAgent.sendMsgWithData(chatId, msg, contentVO.getButtonList());
    }

}
