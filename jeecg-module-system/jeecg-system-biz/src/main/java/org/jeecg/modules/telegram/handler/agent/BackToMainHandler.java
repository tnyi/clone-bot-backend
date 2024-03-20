package org.jeecg.modules.telegram.handler.agent;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"返回"指令
 */
@Slf4j
@Service
public class BackToMainHandler implements BaseBotMsgHandler {

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        TgBot bot = botService.getById(botAgent.getDbBotId());
        MsgContentVO mainMenu = BotUtil.createAgentMenu(bot);
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        botAgent.sendEditMsg(chatId,
                            mainMenu.getText(),
                            mainMenu.getButtonList(),
                            update.getCallbackQuery().getMessage().getMessageId());
        callbackDataService.del(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
    }

}
