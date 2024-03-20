package org.jeecg.modules.telegram.handler.agent.sendmsg;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
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
public class ViewUserInfoHandler extends BaseBotDataHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String dataMapStr = getCallbackData(update);
        Map<String, String> dataMap = JSON.parseObject(dataMapStr, Map.class);

        String tips = "\uD83D\uDCAC <b><a href=\"tg://user?id=" + dataMap.get("userId") + "\">" + dataMap.get("username") + "</a></b> 的id为 <code>" + dataMap.get("userId") + "</code>";
        botAgent.sendReplyMsg(chatId, message.getMessageId(), tips);
    }

}
