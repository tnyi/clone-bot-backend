package org.jeecg.modules.telegram.handler.agent.sendmsg;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 选择管理员消息的接收用户
 */
@Slf4j
@Service
public class SelectRecvUserHandler extends BaseBotDataHandler {

    @Resource
    private ITgChatService tgChatService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String userId = getCallbackData(update);
        TgChat targetChat = tgChatService.lambdaQuery()
                .eq(TgChat::getBotId, botAgent.getDbBotId())
                .eq(TgChat::getChatType, 1)
                .eq(TgChat::getAdminChat, 2)
                .eq(TgChat::getAdminId, userId)
                .last("limit 1").one();

        String dataMapString = callbackDataService.get(BotRedisConstant.ADMIN_SEND_MSG + chatId);
        Map dataMap = JSON.parseObject(dataMapString, Map.class);

        TgBot tgBot = botService.getById(botAgent.getDbBotId());
        botAgent.sendMsg(Long.parseLong(targetChat.getChatId()), BotUtil.checkAdTail(tgBot, (String)dataMap.get("msgText")));
        callbackDataService.del(BotRedisConstant.ADMIN_SEND_MSG + chatId);

        TgChat adminChat = tgChatService.lambdaQuery()
                                        .eq(TgChat::getBotId, botAgent.getDbBotId())
                                        .eq(TgChat::getChatType, 1)
                                        .eq(TgChat::getAdminChat, 1)
                                        .last("limit 1").one();
        adminChat.setMsgRecieverId(userId);
        adminChat.setLastMsgTime(new Date());
        tgChatService.updateById(adminChat);

        GetChatMember getChatMember = GetChatMember.builder().chatId(targetChat.getChatId()).userId(Long.parseLong(targetChat.getAdminId())).build();
        ChatMember chatMember = botAgent.execute(getChatMember);
        String tips = "✅ 此消息已成功发送至： <b><a href=\"https://t.me/" + targetChat.getAdminUsername() + "\">" + BotUtil.getFullname(chatMember.getUser()) + "</a></b>";
        botAgent.sendReplyMsg((Long)dataMap.get("fromChatId"), (Integer)dataMap.get("msgId"), tips);
    }

}
