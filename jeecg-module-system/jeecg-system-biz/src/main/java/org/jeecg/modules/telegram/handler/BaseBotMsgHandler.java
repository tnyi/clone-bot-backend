package org.jeecg.modules.telegram.handler;

import org.jeecg.modules.telegram.bot.BaseBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 机器人消息处理器接口
 */
public interface BaseBotMsgHandler {

    /**
     * 基础消息处理接口
     * @param baseBot  机器人对象
     * @param update   消息通知
     */
    public void process(BaseBot baseBot, Update update);

}
