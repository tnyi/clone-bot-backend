package org.jeecg.modules.telegram.handler.twoway;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.bot.TwoWayBot;
import org.jeecg.modules.telegram.config.BotConfig;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgUser;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgUserService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 处理双向机器人本身的"开始"指令
 */
@Slf4j
@Service
public class TwoWayStartHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotService botService;

    @Resource
    private ITgUserService tgUserService;

    @Resource
    private BotConfig botConfig;

    @Override
    public void process(BaseBot baseBot, Update update) {
        String msg = "\uD83C\uDFE0 你好，管理者！\n" +
                        "\n" +
                        "欢迎使用双向机器人，点击下方的添加按钮添加机器人吧。\n" +
                        "\n" +
                        "欢迎进入交流群交流。";
        Long chatId = null;
        String userId = null;
        if(update.hasMessage()) {
            Message message = update.getMessage();
            chatId = message.getChatId();
            userId = String.valueOf(update.getMessage().getFrom().getId());

            TgUser tgUser = tgUserService.lambdaQuery().eq(TgUser::getTgUserId, userId).one();
            if(tgUser == null) {
                tgUser = new TgUser();
                tgUser.setTgUserId(userId);
                tgUser.setBalance(BigDecimal.ZERO);
                tgUser.setStatus(1);
                tgUser.setChatId(String.valueOf(chatId));
                tgUserService.save(tgUser);
            }
        }
        else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = String.valueOf(update.getCallbackQuery().getFrom().getId());
        }

        TwoWayBot.instance().sendMsgWithData(chatId, msg, botService.twowayBotButton(userId));
    }

}
