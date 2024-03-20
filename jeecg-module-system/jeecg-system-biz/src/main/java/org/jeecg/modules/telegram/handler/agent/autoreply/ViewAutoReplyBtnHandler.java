package org.jeecg.modules.telegram.handler.agent.autoreply;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.constant.BotBizConstant;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置自动回复-某条目-设置按钮-查看"指令
 */
@Slf4j
@Service
public class ViewAutoReplyBtnHandler implements BaseBotMsgHandler {

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String configName = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgAutoReplyConfig config = autoReplyConfigService.getByConfigName(botAgent.getDbBotId(), configName);
        String txt = config != null ? config.getMsgButton() : null;
        String msg = StringUtils.isBlank(txt) ? BotBizConstant.DEFAULT_WELCOME_TEXT : txt;
        botAgent.sendMsg(message.getChatId(), msg);
    }

}
