package org.jeecg.modules.telegram.handler.agent.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgMsgTemplate;
import org.jeecg.modules.telegram.handler.BotMsgFilter;
import org.jeecg.modules.telegram.handler.BotMsgFilterChain;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgMsgTemplateService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.List;

/**
 * 符合消息模板的关键词，回复设定的内容
 */
@Slf4j
@Service
public class MsgTemplateFilter implements BotMsgFilter {

    @Resource
    private ITgBotService botService;

    @Resource
    private ITgMsgTemplateService msgTemplateService;

    @Override
    public void doFilter(BaseBot botAgent, Update update, BotMsgFilterChain filterChain) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String keywords = message.getText();
        if(StringUtils.isBlank(keywords)) {
            filterChain.doFilter(botAgent, update);
            return;
        }
        String searchWord = null;
        if(keywords.startsWith("/") || keywords.startsWith(".")) {
            searchWord = keywords.substring(1, keywords.length());
        } else {
            filterChain.doFilter(botAgent, update);
            return;
        }

        List<TgMsgTemplate> validList = msgTemplateService.lambdaQuery()
                                                            .eq(TgMsgTemplate::getTemplateName, searchWord)
                                                            .eq(TgMsgTemplate::getBotId, botAgent.getDbBotId())
                                                            .list();
        if(validList.size() == 0) {
            //没有符合的可以自动回复的规则
            filterChain.doFilter(botAgent, update);
            return;
        }

        TgBot tgBot = botService.getById(botAgent.getDbBotId());
        for(TgMsgTemplate config : validList) {
            String msg = BotUtil.checkAdTail(tgBot, config.getTextMsg());
            BotUtil.sendTextMsg(botAgent, chatId, config.getMediaType(), msg, config.getMsgButton(), config.getMediaFile());
        }
        filterChain.disableForward();
        filterChain.doFilter(botAgent, update);
    }

}
