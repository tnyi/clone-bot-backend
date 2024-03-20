package org.jeecg.modules.telegram.handler.twoway.upgrade;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotBizConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgPurchaseOrder;
import org.jeecg.modules.telegram.entity.TgRechargeOrder;
import org.jeecg.modules.telegram.entity.TgUser;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgPurchaseOrderService;
import org.jeecg.modules.telegram.service.ITgRechargeOrderService;
import org.jeecg.modules.telegram.service.ITgUserService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * 处理双向机器人的"升级专业版-确认时间-付款"指令
 */
@Slf4j
@Service
public class UpgradeBotConfirmHandler extends BaseBotDataHandler {

    @Resource
    private ITgPurchaseOrderService purchaseOrderService;

    @Resource
    private ITgUserService userService;

    @Resource
    private ITgBotService botService;

    @Override
    @Transactional
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        String data = getCallbackData(update);
        String[] dataArr = data.split("\\|");
        String botId = dataArr[0];
        TgBot bot = botService.getById(botId);
        if(bot.getServiceType()==2 && bot.getExpireTime() != null && (bot.getExpireTime().getTime() - System.currentTimeMillis()) > BotBizConstant.MAX_EXPIRE_TIME){
            String msg = "✅你是永久专业版，无需升级！";
            botAgent.sendMsg(message.getChatId(), msg);
            return;
        }

        Integer servicType = Integer.parseInt(dataArr[1]);
        int[] monthArr = {1,3,6,12,120};
        String amountStr = dataArr[2];
        BigDecimal amount = new BigDecimal(amountStr);
        TgUser tgUser = userService.lambdaQuery().eq(TgUser::getTgUserId, update.getCallbackQuery().getFrom().getId()).last(" for update").one();
        BigDecimal balance = tgUser.getBalance();
        if(tgUser.getBalance().compareTo(amount) < 0) {
            String msg = "\uD83D\uDEAB 余额不足，请先充值";
            botAgent.sendMsg(message.getChatId(), msg);
            return;
        }

        //机器人升级版本
        Date oldExpireTime = bot.getExpireTime();
        Date now = new Date();
        Date begin = oldExpireTime==null || oldExpireTime.before(now) ? now : oldExpireTime;
        Date expireTime = (servicType == 5) ? DateUtil.offsetMonth(begin, 1200) : DateUtil.offsetMonth(begin, monthArr[servicType-1]);
        bot.setServiceType(2);
        bot.setExpireTime(expireTime);
        botService.updateById(bot);

        //用户减少余额
        tgUser.setBalance(balance.subtract(amount));
        userService.updateById(tgUser);

        //记录购买记录
        TgPurchaseOrder order = new TgPurchaseOrder();
        order.setAmount(amount.negate());
        order.setUserId(tgUser.getId());
        order.setServiceType(servicType);
        order.setServiceBeginTime(begin);
        order.setServiceEndTime(expireTime);
        order.setBotId(botId);
        purchaseOrderService.save(order);

        String[] monthDescArr = {"1个月", "3个月", "6个月", "12个月", "永久"};
        botAgent.sendMsg(message.getChatId(), "✅升级成功专业版" + monthDescArr[servicType-1]);
    }

}
