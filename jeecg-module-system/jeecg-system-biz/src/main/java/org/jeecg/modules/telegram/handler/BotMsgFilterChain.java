package org.jeecg.modules.telegram.handler;

import org.jeecg.modules.telegram.bot.BaseBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 消息过滤器链式处理
 */
public interface BotMsgFilterChain {

    public void doFilter(BaseBot bot, Update update);

    public boolean couldForward();

    public void disableForward();

}
