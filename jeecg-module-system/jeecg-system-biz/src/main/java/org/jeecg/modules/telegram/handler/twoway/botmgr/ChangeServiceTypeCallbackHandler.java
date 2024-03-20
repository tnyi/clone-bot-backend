package org.jeecg.modules.telegram.handler.twoway.botmgr;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.bot.TwoWayBot;
import org.jeecg.modules.telegram.config.BotConfig;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.List;

/**
 * 处理"切换免费版和收费版"按钮点击事件
 */
@Slf4j
@Service
public class ChangeServiceTypeCallbackHandler implements BaseBotMsgHandler {

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgBotService botService;

    @Resource
    private BotConfig botConfig;

    @Override
    public void process(BaseBot baseBot, Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String botId = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        callbackDataService.del(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        BotUtil.delTxtRelyHandler(baseBot.getDbBotId(), chatId);

        TgBot tgBot = botService.getById(botId);
        int serviceType = tgBot.getServiceType() == 1 ? 2 : 1;
        if(tgBot != null) {
            tgBot.setServiceType(serviceType);
            botService.updateById(tgBot);
        }
        String msg = "✅ 机器人版本切换为" + ((serviceType==1) ? "[免费版]" : "[专业版]") +"，机器人:@" + tgBot.getBotName();

        String userId = String.valueOf(callbackQuery.getFrom().getId());
        List<TgBot> botList = botService.lambdaQuery().eq(TgBot::getAdminId, userId).list();
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        if(botList.size() > 0) {
            for(TgBot bot : botList) {
                btnBuilder = btnBuilder
                        .addRow()
                        .addUrlButton("⚙️去设置" + bot.getBotName(), "https://t.me/"+bot.getBotName())
                        .addDataButton("\uD83D\uDDD1删除", bot.getId(), ChangeServiceTypeCallbackHandler.class)
                        .addDataButton((bot.getServiceType()==1 ? "[免费版]" : "[专业版]") + " 点击切换", bot.getId(), ChangeServiceTypeCallbackHandler.class);
            }
        }
        btnBuilder = btnBuilder
                .addRow().addDataButton("➕添加机器人", AddBotCallbackHandler.class)
                .addRow().addUrlButton("\uD83D\uDC65帮助交流", botConfig.getCommunicateGroup());

        TwoWayBot.instance().sendEditCaption(chatId, msg, btnBuilder.build(), callbackQuery.getMessage().getMessageId());
    }

}
