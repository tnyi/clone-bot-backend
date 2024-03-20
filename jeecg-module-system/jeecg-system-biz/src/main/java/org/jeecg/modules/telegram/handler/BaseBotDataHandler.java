package org.jeecg.modules.telegram.handler;

import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 携带数据回调的处理器基础类
 */
public abstract class BaseBotDataHandler implements BaseBotMsgHandler {

    private static ITgCallbackDataService callbackDataService = null;

    /**
     * 获取当前会话的回调数据（按钮回调）
     * @param update
     * @return
     */
    public String getCallbackData(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        return getBizData(chatId);
    }

    /**
     * 获取当前会话的回调数据
     * @param chatId
     * @return
     */
    public String getBizData(Long chatId) {
        if(callbackDataService == null) {
            callbackDataService = BotUtil.getBean(ITgCallbackDataService.class);
        }
        return callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
    }

}
