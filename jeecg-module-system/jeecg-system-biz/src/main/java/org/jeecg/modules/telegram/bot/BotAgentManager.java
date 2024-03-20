package org.jeecg.modules.telegram.bot;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理机器人管理类
 */
public class BotAgentManager {

    private static Map<String, BotAgent> agentMap = new ConcurrentHashMap<>();

    public static void addBot(String botId, BotAgent agent) {
        agentMap.put(botId, agent);
    }

    public static BotAgent removeBot(String botId) {
        return agentMap.remove(botId);
    }

    public static BotAgent queryById(String botId) {
        return agentMap.get(botId);
    }

    public static void stopAll() {
        try {
            Collection<BotAgent> allBots = agentMap.values();
            for (BotAgent agent : allBots) {
                agent.shutdown();
            }
            agentMap.clear();
            TwoWayBot.instance().shutdown();
        } catch (Exception e) {}
    }

}
