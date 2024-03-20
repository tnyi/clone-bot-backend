package org.jeecg.modules.telegram.handler.twoway.upgrade;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotBizConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理双向机器人的"升级专业版-选择具体机器升级"指令
 */
@Slf4j
@Service
public class StartUpgradeBotHandler extends BaseBotDataHandler {

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String botId = getCallbackData(update);
        TgBot bot = botService.getById(botId);
        if(bot.getServiceType()==2 && bot.getExpireTime() != null && (bot.getExpireTime().getTime() - System.currentTimeMillis()) > BotBizConstant.MAX_EXPIRE_TIME){
            String msg = "✅你是永久专业版，无需升级！";
            botAgent.sendMsg(message.getChatId(), msg);
            return;
        }

        String msg = "<b>升级专业版价格</b> \uD83D\uDC47\n" +
                "\n" +
                "1个月3USDT       3个月6USDT\n" +
                "6个月8USDT      12个月13USDT\n" +
                "              永久 28.8USDT\n" +
                "\n" +
                "<b>请选择使用时间</b> \uD83D\uDC47";
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder()
                                                            .addRow()
                                                            .addDataButton("1个月", botId + "|1|3", UpgradeBotConfirmHandler.class)
                                                            .addDataButton("3个月", botId + "|2|6", UpgradeBotConfirmHandler.class)
                                                            .addDataButton("6个月", botId + "|3|8", UpgradeBotConfirmHandler.class)
                                                            .addDataButton("12个月", botId + "|4|13", UpgradeBotConfirmHandler.class)
                                                            .addDataButton("永久", botId + "|5|28.8", UpgradeBotConfirmHandler.class)
                                                            .addRow()
                                                            .addDataButton("返回", UpgradeBotHandler.class);
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }

}
