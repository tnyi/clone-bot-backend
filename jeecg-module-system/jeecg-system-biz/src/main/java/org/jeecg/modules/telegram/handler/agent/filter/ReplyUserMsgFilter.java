package org.jeecg.modules.telegram.handler.agent.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BotMsgFilter;
import org.jeecg.modules.telegram.handler.BotMsgFilterChain;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 符合自动回复的关键词，自动回复设定的内容
 */
@Slf4j
@Service
public class ReplyUserMsgFilter implements BotMsgFilter {

    @Resource
    private ITgBotService botService;

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Override
    public void doFilter(BaseBot botAgent, Update update, BotMsgFilterChain filterChain) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();

        TgBot bot = botService.getById(botAgent.getDbBotId());
        String adminId = bot.getAdminId();

        String keywords = message.getText();
        List<TgAutoReplyConfig> validList = new ArrayList<>();
        List<TgAutoReplyConfig> autoReplyList = autoReplyConfigService.lambdaQuery()
                                                            .like(TgAutoReplyConfig::getKeywordList, keywords)
                                                            .eq(TgAutoReplyConfig::getBotId, botAgent.getDbBotId())
                                                            .list();
        for(TgAutoReplyConfig config : autoReplyList) {
            if(config.getOnlyForAdmin() == 1 && !adminId.equals(userId.toString())) {
                //判断是否只能管理员触发
                continue;
            }
            if(StringUtils.isBlank(config.getTextMsg())) {
                //自动回复文本消息没有设置则不触发
                continue;
            }
            String configWords = config.getKeywordList();
            if(config.getMatchType() == 2) {
                boolean match = false;
                String[] wordList = configWords.split("\\|");
                for(String word : wordList) {
                    if(word.equalsIgnoreCase(keywords)) {
                        match = true;
                        break;
                    }
                }
                if(match) {
                    validList.add(config);
                }
            } else {
                validList.add(config);
            }
        }

        if(validList.size() == 0) {
            //没有符合的可以自动回复的规则
            filterChain.doFilter(botAgent, update);
            return;
        }

        for(TgAutoReplyConfig config : validList) {
            String msg = BotUtil.checkAdTail(bot, config.getTextMsg());
            BotUtil.sendTextMsg(botAgent, chatId, config.getMediaType(), msg, config.getMsgButton(), config.getMediaFile(), message.getMessageId(), null);
        }
        filterChain.disableForward();
        filterChain.doFilter(botAgent, update);
    }

}
