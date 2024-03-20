package org.jeecg.modules.telegram.handler.twoway.querytime;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotBizConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.ViewEmptyBtnHandler;
import org.jeecg.modules.telegram.handler.twoway.upgrade.StartUpgradeBotHandler;
import org.jeecg.modules.telegram.handler.twoway.TwoWayStartHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.List;

/**
 * 处理双向机器人的"查询时间"指令
 */
@Slf4j
@Service
public class QueryBotTimeHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String userId = String.valueOf(update.getCallbackQuery().getFrom().getId());
        List<TgBot> botList = botService.lambdaQuery().eq(TgBot::getAdminId, userId).list();
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        if(botList.size() > 0) {
            for(TgBot bot : botList) {
                boolean forever = false;
                String expireStr = "";
                if(bot.getServiceType() == 2 && bot.getExpireTime() != null && (bot.getExpireTime().getTime() - System.currentTimeMillis()) > BotBizConstant.MAX_EXPIRE_TIME){
                    expireStr = "✅永久使用";
                    forever = true;
                } else {
                    if(bot.getExpireTime() == null) {
                        expireStr = "免费版";
                    } else {
                        expireStr = DateUtils.formatDate(bot.getExpireTime());
                    }
                }
                btnBuilder = btnBuilder
                        .addRow()
                        .addUrlButton(bot.getBotName(), "https://t.me/" + bot.getBotName())
                        .addDataButton(bot.getServiceType() == 1 ? "免费版" : expireStr, bot.getId(), ViewEmptyBtnHandler.class);
                if(!forever) {
                    btnBuilder.addDataButton("续费", StartUpgradeBotHandler.class);
                }
            }
        }
        btnBuilder = btnBuilder.addRow().addDataButton("返回", TwoWayStartHandler.class);
        String msg = "到期时间查询";
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }

}
