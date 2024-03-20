package org.jeecg.modules.telegram.handler.agent.filter;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.handler.BotMsgFilter;
import org.jeecg.modules.telegram.handler.BotMsgFilterChain;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 检测聊天会话是否已经保存
 */
@Slf4j
@Service
public class CheckChatInfoFilter implements BotMsgFilter {

    @Resource
    private ITgChatService tgChatService;

    @Resource
    private ITgBotService botService;

    @Override
    public void doFilter(BaseBot botAgent, Update update, BotMsgFilterChain filterChain) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        TgChat tgChat = tgChatService.lambdaQuery().eq(TgChat::getBotId, botAgent.getDbBotId()).eq(TgChat::getChatId, chatId).last("limit 1").one();
        if(tgChat == null) {
            TgBot tgBot = botService.getById(botAgent.getDbBotId());
            String chatType = update.getMessage().getChat().getType();
            tgChat = new TgChat();
            tgChat.setChatId(String.valueOf(chatId));
            tgChat.setBotId(botAgent.getDbBotId());
            tgChat.setChatType("private".equals(chatType) ? 1 : 2);
            tgChat.setAdminChat(tgBot.getAdminId().equals(message.getFrom().getId().toString()) ? 1 : 2);
            tgChat.setAdminUsername(message.getFrom().getUserName());
            tgChat.setAdminId(String.valueOf(message.getFrom().getId()));
            tgChat.setBotIsAdmin(1);
            tgChat.setStatus(1);
            tgChatService.save(tgChat);

            //同步账号到web im
            tgChatService.registerUserToWebIM(botAgent, tgChat);
        }

        filterChain.doFilter(botAgent, update);
    }

}
