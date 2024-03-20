package org.jeecg.modules.telegram.handler.agent.filter;

import cn.hutool.cron.timingwheel.SystemTimer;
import cn.hutool.cron.timingwheel.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BotMsgFilter;
import org.jeecg.modules.telegram.handler.BotMsgFilterChain;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.util.List;

/**
 * 新用户进群欢迎语
 */
@Slf4j
@Service
public class GroupWelcomeFilter implements BotMsgFilter {

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Resource
    private ITgBotService tgBotService;

    @Override
    public void doFilter(BaseBot botAgent, Update update, BotMsgFilterChain filterChain) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if(!message.isGroupMessage()) {
            filterChain.doFilter(botAgent, update);
            return;
        }

        TgBot tgBot = tgBotService.getById(botAgent.getDbBotId());
        if(tgBot.getJoinAutoReply() != 1) {
            filterChain.doFilter(botAgent, update);
            return;
        }

        List<User> newUserList = message.getNewChatMembers();
        if(CollectionUtils.isEmpty(newUserList)) {
            filterChain.doFilter(botAgent, update);
            return;
        }

        List<TgWelcomeConfig> configList = welcomeConfigService.lambdaQuery()
                                                            .eq(TgWelcomeConfig::getBotId, botAgent.getDbBotId())
                                                            .eq(TgWelcomeConfig::getConfigType, 2)
                                                            .list();
        if(CollectionUtils.isEmpty(configList)) {
            //没有设置进群欢迎语
            filterChain.doFilter(botAgent, update);
            return;
        }

        GetChat getChat = GetChat.builder().chatId(chatId).build();
        Chat chatInfo = botAgent.execute(getChat);
        String groupId = chatInfo.getId().toString();
        String groupName = chatInfo.getTitle();
        SystemTimer systemTimer = new SystemTimer();
        systemTimer.start();
        for(User user : newUserList) {
            Long userId = user.getId();
            String username = user.getUserName();
            for(TgWelcomeConfig config : configList) {
                String msg = BotUtil.checkAdTail(tgBot, config.getTextMsg());
                String nickname = BotUtil.getFullname(message.getFrom());
                msg = msg.replace("{id}", userId.toString());
                msg = msg.replace("{id_code}", "<code>" + userId + "</code>");
                msg = msg.replace("{display_name}", nickname);
                msg = msg.replace("{mention}", "<a href=\"tg://user?id=" + userId + "\">" + nickname + "</a>");
                msg = msg.replace("{username}", username);
                msg = msg.replace("{group_id}", groupId);
                msg = msg.replace("{group_id_code}", "<code>" + groupId + "</code>");
                msg = msg.replace("{group_name}", groupName);
                Message sendMsg = BotUtil.sendTextMsg(botAgent, chatId, config.getMediaType(), msg, config.getMsgButton(), config.getMediaFile());
                if(sendMsg != null && config.getSaveTime() != 0) {
                    systemTimer.addTask(new TimerTask(() -> {
                            log.info("执行延时删除欢迎语任务:{}", LocalTime.now());
                        DeleteMessage deleteMessage = DeleteMessage.builder().chatId(chatId).messageId(sendMsg.getMessageId()).build();
                        botAgent.execute(deleteMessage);
                        }, config.getSaveTime() * 1000));
                }
            }
        }
        filterChain.disableForward();
        filterChain.doFilter(botAgent, update);
    }

}
