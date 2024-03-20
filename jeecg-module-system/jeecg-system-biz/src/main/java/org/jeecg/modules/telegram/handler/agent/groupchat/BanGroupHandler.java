package org.jeecg.modules.telegram.handler.agent.groupchat;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import javax.annotation.Resource;
import java.util.List;

/**
 * 禁止群组
 */
@Slf4j
@Service
public class BanGroupHandler extends BaseBotDataHandler {

    @Resource
    private ITgChatService chatService;

    @Resource
    private ViewGroupInfoHandler viewGroupInfoHandler;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String groupChatId = getCallbackData(update);
        TgChat tgChat = chatService.lambdaQuery().eq(TgChat::getBotId, botAgent.getDbBotId()).eq(TgChat::getChatId, groupChatId).one();
        tgChat.setStatus(2);
        chatService.updateById(tgChat);

        redisUtil.set(BotRedisConstant.GROUP_CHAT_EDIT_TYPE + chatId, 1, 10);
        viewGroupInfoHandler.process(botAgent, update);
    }

}
