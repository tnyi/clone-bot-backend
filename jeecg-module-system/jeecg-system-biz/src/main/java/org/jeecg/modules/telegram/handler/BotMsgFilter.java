package org.jeecg.modules.telegram.handler;

import org.jeecg.modules.telegram.bot.BaseBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 消息过滤器
 */
public interface BotMsgFilter {

    public void doFilter(BaseBot bot, Update update, BotMsgFilterChain filterChain);

}
