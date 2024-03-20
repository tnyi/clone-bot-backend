package org.jeecg.modules.telegram.handler.agent.filter;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.*;
import org.jeecg.modules.telegram.handler.BotMsgFilter;
import org.jeecg.modules.telegram.handler.BotMsgFilterChain;
import org.jeecg.modules.telegram.service.*;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理代理机器人的"/ban  拉黑名单"指令
 */
@Slf4j
@Service
public class BanUserFilter implements BotMsgFilter {

    @Resource
    private ITgBotService tgBotService;

    @Resource
    private ITgBlacklistService blacklistService;

    @Resource
    private ITgBotAdminService botAdminService;

    @Resource
    private ITgChatService tgChatService;

    @Override
    public void doFilter(BaseBot botAgent, Update update, BotMsgFilterChain filterChain) {
        Message message = update.getMessage();
        String txt = message.getText();
        if(!"/ban".equals(txt)) {
            filterChain.doFilter(botAgent, update);
            return;
        }

        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        TgBot tgBot = tgBotService.getById(botAgent.getDbBotId());

        List<TgBotAdmin> botAdminList = botAdminService.lambdaQuery().eq(TgBotAdmin::getBotId, botAgent.getDbBotId()).list();
        List<String> adminChatIdList = botAdminList.stream().map(e -> e.getChatId()).collect(Collectors.toList());
        //只有管理员可以拉黑用户
        if(!userId.toString().equals(tgBot.getAdminId()) && !adminChatIdList.contains(chatId.toString())) {
            filterChain.doFilter(botAgent, update);
            return;
        }

        Message adminReplyMsg = message.getReplyToMessage();
        if(adminReplyMsg != null) {
            User forwardUser = adminReplyMsg.getForwardFrom();
            if(forwardUser != null) {
                TgChat chat = tgChatService.lambdaQuery()
                        .eq(TgChat::getBotId, botAgent.getDbBotId())
                        .eq(TgChat::getAdminId, forwardUser.getId())
                        .eq(TgChat::getChatType, 1)
                        .eq(TgChat::getAdminChat, 2).one();
                String nickname = BotUtil.getFullname(message.getFrom());
                String tips = "请选择封禁 <b><a href=\"tg://user?id=" + userId + "\">" + nickname + "</a></b> 的天数";
                MsgContentVO vo = blacklistService.banUserMsg(botAgent, forwardUser, Long.parseLong(chat.getChatId()), null);
                botAgent.sendMsgWithData(chatId, tips, vo.getButtonList());

                filterChain.disableForward();
                filterChain.doFilter(botAgent, update);
                return;
            }
        }

        filterChain.doFilter(botAgent, update);
    }

}
