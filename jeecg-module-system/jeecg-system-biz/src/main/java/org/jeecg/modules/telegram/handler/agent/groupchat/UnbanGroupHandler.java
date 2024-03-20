package org.jeecg.modules.telegram.handler.agent.groupchat;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 开启群组
 */
@Slf4j
@Service
public class UnbanGroupHandler extends BaseBotDataHandler {

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
        tgChat.setStatus(1);
        chatService.updateById(tgChat);

        redisUtil.set(BotRedisConstant.GROUP_CHAT_EDIT_TYPE + chatId, 1, 10);
        viewGroupInfoHandler.process(botAgent, update);
    }

}
