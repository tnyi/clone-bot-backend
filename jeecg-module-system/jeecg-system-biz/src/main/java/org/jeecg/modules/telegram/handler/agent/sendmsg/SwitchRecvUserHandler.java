package org.jeecg.modules.telegram.handler.agent.sendmsg;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * 切换消息的接收用户
 */
@Slf4j
@Service
public class SwitchRecvUserHandler extends BaseBotDataHandler {

    @Resource
    private ITgChatService tgChatService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String dataMapStr = getCallbackData(update);
        Map<String, String> dataMap = JSON.parseObject(dataMapStr, Map.class);

        TgChat adminChat = tgChatService.lambdaQuery()
                                        .eq(TgChat::getBotId, botAgent.getDbBotId())
                                        .eq(TgChat::getChatType, 1)
                                        .eq(TgChat::getAdminChat, 1)
                                        .last("limit 1").one();
        adminChat.setMsgRecieverId(dataMap.get("userId"));
        adminChat.setLastMsgTime(new Date());
        tgChatService.updateById(adminChat);

        String tips = "✅ 聊天对象已切换至：<b><a href=\"tg://user?id=" + dataMap.get("userId") + "\">" + dataMap.get("username") + "</a></b>";
        botAgent.sendMsg(chatId, tips);
    }

}
