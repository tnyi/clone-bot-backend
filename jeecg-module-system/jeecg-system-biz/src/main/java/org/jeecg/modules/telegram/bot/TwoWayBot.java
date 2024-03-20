package org.jeecg.modules.telegram.bot;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.filter.AgentMsgFilterChain;
import org.jeecg.modules.telegram.handler.twoway.TwoWayStartHandler;
import org.jeecg.modules.telegram.handler.twoway.recharge.AdminUpdateUserCashHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotSession;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 双向机器人
 */
@Slf4j
public class TwoWayBot extends BaseBot {

    private ITgBotService tgBotService = BotUtil.getBean(ITgBotService.class);

    /**单例模式*/
    private static TwoWayBot single = null;

    /**消息处理器*/
    private LinkedHashMap<String, BaseBotMsgHandler> msgHandlerMap = new LinkedHashMap<String, BaseBotMsgHandler>() {{
        put("/start", BotUtil.getBean(TwoWayStartHandler.class));
    }};

    /**管理员调整用户资金*/
    private AdminUpdateUserCashHandler adminUpdateUserCashHandler = BotUtil.getBean(AdminUpdateUserCashHandler.class);

    /**机器人token*/
    public static String botToken = "";

    /**机器人用户名*/
    public static String botUsername = "";

    /**机器人通信会话*/
    private BotSession myBotSession = null;

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public LinkedHashMap<String, BaseBotMsgHandler> getCommandHandler() {
        return this.msgHandlerMap;
    }

    @Override
    public String getDbBotId() {
        return null;
    }

    public TwoWayBot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    /**
     * 关闭机器人
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

    /**
     * 启动双向机器人实例
     * @return
     */
    public static TwoWayBot instance() {
        if(single == null) {
            single = new TwoWayBot(BotUtil.createBotOption());
            single.myBotSession = BotUtil.initBot(single);

            //启动所有代理机器人
            List<TgBot> tgBotList = single.tgBotService.list();
            for(TgBot bot : tgBotList) {
                BotAgent botAgent = BotAgent.create(bot.getBotToken(), bot.getBotName());
                botAgent.setDbBotId(bot.getId());
                botAgent.setTgBotId(bot.getTgBotId());
                BotAgentManager.addBot(bot.getId(), botAgent);
            }
        }
        return single;
    }

    @Override
    public void onUpdateReceived(Update update) {
        TwoWayBot bot = this;
        new Thread() {
            public void run() {
                bot.processUpdate(update);
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
            adminUpdateUserCashHandler.process(this, update);
        }
    }
}
