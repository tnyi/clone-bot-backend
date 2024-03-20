package org.jeecg.modules.telegram.handler.agent.filter;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgBotAdmin;
import org.jeecg.modules.telegram.handler.BotMsgFilter;
import org.jeecg.modules.telegram.handler.BotMsgFilterChain;
import org.jeecg.modules.telegram.handler.agent.blacklist.AddBlacklistHandler;
import org.jeecg.modules.telegram.handler.agent.blacklist.UnlockUserHandler;
import org.jeecg.modules.telegram.service.ITgBlacklistService;
import org.jeecg.modules.telegram.service.ITgBotAdminService;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理代理机器人的"/unban  解封黑名单"指令
 */
@Slf4j
@Service
public class UnbanUserFilter implements BotMsgFilter {

    @Resource
    private ITgBotService tgBotService;

    @Resource
    private ITgBlacklistService blacklistService;

    @Resource
    private ITgBotAdminService botAdminService;

    @Override
    public void doFilter(BaseBot botAgent, Update update, BotMsgFilterChain filterChain) {
        Message message = update.getMessage();
        String txt = message.getText();
        if(!"/unban".equals(txt)) {
            filterChain.doFilter(botAgent, update);
            return;
        }

        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        TgBot tgBot = tgBotService.getById(botAgent.getDbBotId());

        List<TgBotAdmin> botAdminList = botAdminService.lambdaQuery().eq(TgBotAdmin::getBotId, botAgent.getDbBotId()).list();
        List<String> adminChatIdList = botAdminList.stream().map(e -> e.getChatId()).collect(Collectors.toList());
        //只有管理员可以解封用户
        if(!userId.toString().equals(tgBot.getAdminId()) && !adminChatIdList.contains(chatId.toString())) {
            filterChain.doFilter(botAgent, update);
            return;
        }

        Message adminReplyMsg = message.getReplyToMessage();
        if(adminReplyMsg != null) {
            User forwardUser = adminReplyMsg.getForwardFrom();
            if(forwardUser != null) {
                TgBlacklist blacklist = blacklistService.lambdaQuery().eq(TgBlacklist::getBotId, botAgent.getDbBotId()).eq(TgBlacklist::getTgUserId, forwardUser.getId()).one();
                if(blacklist != null) {
                    blacklistService.removeById(blacklist);
                }

                String nickname = BotUtil.getFullname(message.getFrom());
                String tips = "<b><a href=\"tg://user?id=" + forwardUser.getId() + "\">" + nickname + "</a></b> 已解封";
                InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
                btnBuilder.addRow()
                        .addDataButton("\uD83D\uDEB7拉黑用户", AddBlacklistHandler.class)
                        .addDataButton("\uD83D\uDFE2解封用户", UnlockUserHandler.class);
                botAgent.sendMsgWithData(chatId, tips, btnBuilder.build());

                filterChain.disableForward();
                filterChain.doFilter(botAgent, update);
                return;
            }
        }

        filterChain.doFilter(botAgent, update);
    }

}
