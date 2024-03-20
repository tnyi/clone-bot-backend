package org.jeecg.modules.telegram.handler.twoway.botmgr;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.handler.twoway.TwoWayStartHandler;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.bot.BotAgent;
import org.jeecg.modules.telegram.bot.BotAgentManager;
import org.jeecg.modules.telegram.bot.TwoWayBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.List;

/**
 * 处理"删除机器人"按钮点击事件
 */
@Slf4j
@Service
public class DelBotCallbackHandler implements BaseBotMsgHandler {

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgBotService botService;

    @Resource
    private TwoWayStartHandler twoWayStartHandler;

    @Override
    public void process(BaseBot baseBot, Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String botId = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        callbackDataService.del(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        BotUtil.delTxtRelyHandler(baseBot.getDbBotId(), chatId);
        TgBot tgBot = botService.getById(botId);
        if(tgBot != null) {
            botService.removeById(tgBot.getId());
        }

        String msg = "✅ 删除机器人完成，机器人:@" + tgBot.getBotName();
        TwoWayBot.instance().sendMsg(chatId, msg);

        twoWayStartHandler.process(baseBot, update);

        new Thread() {
            public void run() {
                BotAgent botAgent = BotAgentManager.removeBot(tgBot.getId());
                if(botAgent != null) {
                    botAgent.shutdown();
                    botAgent = null;
                }
            }
        }.start();
    }

}
