package org.jeecg.modules.telegram.bot;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.BotMsgFilter;
import org.jeecg.modules.telegram.handler.agent.filter.*;
import org.jeecg.modules.telegram.util.BotUtil;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * 代理机器人
 */
@Slf4j
public class BotAgent extends BaseBot {

    /**单例模式*/
    private static BotAgent single = null;

    /**消息处理器*/
    private LinkedHashMap<String, BaseBotMsgHandler> msgHandlerMap = new LinkedHashMap<String, BaseBotMsgHandler>() {{

    }};

    /**用户消息过滤器 @@*/
    private BotMsgFilter[] textMsgfilterArray = new BotMsgFilter[]{
            BotUtil.getBean(AgentStartFilter.class),
            BotUtil.getBean(BanUserFilter.class),
            BotUtil.getBean(UnbanUserFilter.class),
            BotUtil.getBean(CheckChatInfoFilter.class),
            BotUtil.getBean(ReplyUserMsgFilter.class),
            BotUtil.getBean(GroupWelcomeFilter.class),
            BotUtil.getBean(MsgTemplateFilter.class),
            BotUtil.getBean(ForwardTextMsgFilter.class)
    };

    /**机器人token*/
    private String botToken = null;

    /**机器人用户名*/
    private String botUsername = null;

    /**机器人通信会话*/
    private BotSession myBotSession = null;

    /**数据中的机器人ID*/
    private String dbBotId = null;

    /**tg的botId*/
    private String tgBotId = null;

    public BotAgent(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @Override
    public LinkedHashMap<String, BaseBotMsgHandler> getCommandHandler() {
        return this.msgHandlerMap;
    }

    @Override
    public void onUpdateReceived(Update update) {
        BotAgent agent = this;
        new Thread() {
            public void run() {
                agent.processUpdate(update);
                agentBizHandler(update);
            }
        }.start();
    }

    /**
     * 群用户消息处理
     * @param update
     */
    public void agentBizHandler(Update update) {
        if (update.hasMessage()) {
            AgentMsgFilterChain filterChain = new AgentMsgFilterChain(textMsgfilterArray);
            filterChain.doFilter(this, update);
        }
    }

    /**
     * 根据token和username创建tg 机器人
     *
     * @param token
     * @param username
     * @return
     */
    public static BotAgent create(String token, String username) {
        BotAgent single = new BotAgent(BotUtil.createBotOption());
        single.botToken = token;
        single.botUsername = username;
        single.myBotSession = BotUtil.initBot(single);
        return single;
    }

    /**
     * 关闭代理机器人
     */
    public void shutdown() {
        try {
            if (myBotSession != null) {
                myBotSession.stop();
                myBotSession = null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    public String getDbBotId() {
        return dbBotId;
    }

    public void setDbBotId(String dbBotId) {
        this.dbBotId = dbBotId;
    }

    public String getTgBotId() {
        return tgBotId;
    }

    public void setTgBotId(String tgBotId) {
        this.tgBotId = tgBotId;
    }

}
