package org.jeecg.modules.telegram.handler.agent.bottombtn;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.annotation.Resource;
import java.util.*;

/**
 * 处理代理机器人的"设置底部按钮-成功"指令
 */
@Slf4j
@Service
public class SetBottomButtonTextOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotService botService;

    @Resource
    private ITgChatService tgChatService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        TgBot bot = botService.getById(botAgent.getDbBotId());

        Set<KeyboardRow> keyboardRowSet = BotUtil.parseBottomBtn(message.getText());
        int btnCount = keyboardRowSet.size();
        if(bot.getServiceType() == 1 && btnCount > 5) {
            String msg = "⚠️ 免费版最多设置5个底部按钮，请开通专业版！";
            BotUtil.upgradeTips(botAgent, message.getChatId(), msg);
            return;
        }

        bot.setBottomButton(message.getText());
        botService.updateById(bot);

        String msg = "✅ 底部按钮设置完成\n" +
                "\n" + message.getText();
        botAgent.sendMsg(message.getChatId(), msg);

        List<TgChat> tgChatList = tgChatService.lambdaQuery()
                .eq(TgChat::getBotId, botAgent.getDbBotId())
                .eq(TgChat::getChatType, 1)
                .eq(TgChat::getAdminChat, 2).list();
        for(TgChat chat : tgChatList) {
            msg = "✅ 底部按钮已更新";
            msg = BotUtil.checkAdTail(bot, msg);
            ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder().keyboard(keyboardRowSet).resizeKeyboard(true).build();
            botAgent.execute(SendMessage.builder().chatId(chat.getChatId()).text(msg).parseMode("html").replyMarkup(replyKeyboardMarkup).build());
        }
    }

}
