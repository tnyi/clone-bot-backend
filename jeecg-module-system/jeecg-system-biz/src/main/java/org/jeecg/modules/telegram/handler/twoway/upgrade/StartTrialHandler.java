package org.jeecg.modules.telegram.handler.twoway.upgrade;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 处理双向机器人的"升级专业版-试用"指令
 */
@Slf4j
@Service
public class StartTrialHandler extends BaseBotDataHandler {

    @Resource
    private ITgBotService botService;

    @Resource
    private UpgradeBotHandler upgradeBotHandler;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String botId = getCallbackData(update);
        TgBot bot = botService.getById(botId);
        Date begin = new Date();
        if(bot.getServiceType() == 2 && begin.before(bot.getExpireTime())) {
            String msg = "该机器人已升级专业版";
            botAgent.sendMsg(message.getChatId(), msg);
            return;
        }

        if(bot.getTrialStatu() == 2) {
            String msg = "该机器人已经试用过，请购买专业版";
            botAgent.sendMsg(message.getChatId(), msg);
            return;
        }

        Date end = new Date(begin.getTime() + 2626560000l);
        bot.setTrialStatu(2);
        bot.setTrialBeginTime(begin);
        bot.setTrialEndTime(end);
        bot.setServiceType(2);
        bot.setExpireTime(end);
        botService.updateById(bot);
        upgradeBotHandler.process(botAgent, update);
    }

}
