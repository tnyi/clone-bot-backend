package org.jeecg.modules.telegram.handler.twoway.recharge;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgUser;
import org.jeecg.modules.telegram.entity.TgUserCashLog;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgUserCashLogService;
import org.jeecg.modules.telegram.service.ITgUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理双向机器人的"管理员修改用户资金"指令
 */
@Slf4j
@Service
public class AdminUpdateUserCashHandler implements BaseBotMsgHandler {

    @Resource
    private ITgUserService userService;

    @Resource
    private ITgUserCashLogService userCashLogService;

    /**可以调整用户资金的管理员tg id*/
    private static String adminId = "6819100359";

    @Transactional
    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String fromUserId = String.valueOf(message.getFrom().getId());

        String text = message.getText();
        String regex = "\\d{10}[+-]\\d+(\\.\\d{1,2})?";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.matches()) {
            if(!adminId.equals(fromUserId)) {
                return;
            }
            String userId = text.substring(0, 10);
            String opt = text.substring(10, 11);
            String amountStr = text.substring(11);
            BigDecimal amount = new BigDecimal(amountStr);
            TgUser tgUser = userService.lambdaQuery().eq(TgUser::getTgUserId, userId).last(" for update").one();
            if(tgUser == null) {
                String msg = "用户ID不存在";
                botAgent.sendMsg(chatId, msg);
                return;
            }
            //记录资金流水
            TgUserCashLog cashLog = new TgUserCashLog();
            BigDecimal balance = tgUser.getBalance();
            String msg = "✅ 充值成功" + amountStr + "U";
            if("+".equals(opt)) {
                tgUser.setBalance(balance.add(amount));
                cashLog.setCashType(1);
            } else {
                msg = "✅ 扣除成功" + amountStr + "U";
                tgUser.setBalance(balance.subtract(amount));
                cashLog.setCashType(3);
            }
            userService.updateById(tgUser);

            cashLog.setBalance(balance);
            cashLog.setCashAmount(amount);
            cashLog.setUserId(userId);
            userCashLogService.save(cashLog);

            botAgent.sendMsg(chatId, msg);
            if(!adminId.equals(tgUser.getTgUserId())) {
                botAgent.sendMsg(Long.parseLong(tgUser.getChatId()), msg);
            }
        }
    }

}
