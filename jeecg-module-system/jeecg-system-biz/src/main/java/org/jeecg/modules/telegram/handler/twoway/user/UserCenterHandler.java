package org.jeecg.modules.telegram.handler.twoway.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgUser;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.twoway.TwoWayStartHandler;
import org.jeecg.modules.telegram.service.ITgUserService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.Resource;

/**
 * 处理双向机器人的"用户中心"指令
 */
@Slf4j
@Service
public class UserCenterHandler implements BaseBotMsgHandler {

    @Resource
    private ITgUserService userService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        User from = update.getCallbackQuery().getFrom();
        TgUser tgUser = userService.lambdaQuery().eq(TgUser::getTgUserId, from.getId()).one();
        String msg = "<b>个人中心</b>\n" +
                        "ID: <code>" + from.getId() + "</code>\n" +
                        "昵称: " + (StringUtils.isBlank(from.getFirstName()) ? "" : from.getFirstName()) + " " + (StringUtils.isBlank(from.getLastName()) ? "" : from.getLastName()) + "\n" +
                        "用户名: " + from.getUserName() + "\n" +
                        "余额: " + tgUser.getBalance().stripTrailingZeros().toPlainString() + " USDT\n";
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder()
                                                            .addRow()
                                                            .addDataButton("返回", TwoWayStartHandler.class);
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }

}
