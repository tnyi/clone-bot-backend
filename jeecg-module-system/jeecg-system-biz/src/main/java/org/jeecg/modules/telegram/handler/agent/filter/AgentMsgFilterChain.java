package org.jeecg.modules.telegram.handler.agent.filter;

import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.handler.BotMsgFilter;
import org.jeecg.modules.telegram.handler.BotMsgFilterChain;
import org.jeecg.modules.telegram.service.ITgBlacklistService;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AgentMsgFilterChain implements BotMsgFilterChain {

    /**消息过滤器数组*/
    private BotMsgFilter[] filterArray = null;

    /**当前正在处理的过滤器在数组中的位置*/
    private int pos = 0;

    /**消息能否转发*/
    private boolean forwardStatus = true;

    private ITgBlacklistService blacklistService = BotUtil.getBean(ITgBlacklistService.class);

    private ITgChatService chatService = BotUtil.getBean(ITgChatService.class);

    public AgentMsgFilterChain(BotMsgFilter[] filters) {
        this.filterArray = filters;
    }

    @Override
    public void doFilter(BaseBot bot, Update update) {
        Message message = update.getMessage();
        TgBlacklist blacklist = blacklistService.getByUserId(bot.getDbBotId(), message.getFrom().getId().toString());
        if(blacklist != null) {
            bot.sendMsg(message.getChatId(), "❌已被拉入黑名单！");
            return;
        }

        if(message.getChat().isGroupChat()) {
            TgChat chat = chatService.lambdaQuery().eq(TgChat::getBotId, bot.getDbBotId()).eq(TgChat::getChatId, message.getChat().getId()).last("limit 1").one();
            if(chat != null && chat.getStatus() == 2) {
                //群聊被禁用
                return;
            }
        }

        if(pos < filterArray.length) {
            BotMsgFilter filter = filterArray[pos];
            pos++;
            filter.doFilter(bot, update, this);
        }
    }

    @Override
    public boolean couldForward() {
        return forwardStatus;
    }

    @Override
    public void disableForward() {
        this.forwardStatus = false;
    }

}
