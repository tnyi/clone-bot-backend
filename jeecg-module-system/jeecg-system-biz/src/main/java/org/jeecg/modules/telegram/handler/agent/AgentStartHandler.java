package org.jeecg.modules.telegram.handler.agent;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"开始"指令
 */
@Slf4j
@Service
public class AgentStartHandler extends BaseBotDataHandler {

    @Resource
    private ITgBotService tgBotService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        TgBot tgBot = tgBotService.getById(botAgent.getDbBotId());

        if(!userId.toString().equals(tgBot.getAdminId())) {
            callbackDataService.del(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
            MsgContentVO mainMenu = BotUtil.createAgentMenu(tgBot);
            botAgent.sendEditMsg(chatId, mainMenu.getText(), mainMenu.getButtonList(), message.getMessageId());
        }
    }

}
