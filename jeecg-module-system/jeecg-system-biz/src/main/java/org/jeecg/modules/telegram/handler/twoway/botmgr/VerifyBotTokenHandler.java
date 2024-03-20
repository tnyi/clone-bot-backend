package org.jeecg.modules.telegram.handler.twoway.botmgr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.bot.BotAgent;
import org.jeecg.modules.telegram.bot.BotAgentManager;
import org.jeecg.modules.telegram.bot.TwoWayBot;
import org.jeecg.modules.telegram.config.BotConfig;
import org.jeecg.modules.telegram.constant.BotBizConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

/**
 * 双向机器人验证用户添加的token
 */
@Slf4j
@Service
public class VerifyBotTokenHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotService botService;

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Resource
    private BotConfig botConfig;

    @Override
    public void process(BaseBot baseBot, Update update) {
        Long chatId = update.getMessage().getChatId();
        String userId = String.valueOf(update.getMessage().getFrom().getId());
        Integer replyMsgId = update.getMessage().getMessageId();
        BotUtil.delTxtRelyHandler(baseBot.getDbBotId(), chatId);
        TwoWayBot.instance().sendMsg(chatId, "\uD83D\uDCE2 正在部署，请稍等...");

        String token = update.getMessage().getText();
        TgBot existBot = botService.getByToken(token);
        if(existBot != null) {
            if(existBot.getAdminId().equals(userId)) {
                TwoWayBot.instance().sendReplyMsg(chatId, replyMsgId, "❌ 该机器人已存在，无需重复添加");
                return;
            }
            botService.removeById(existBot.getId());
            BotAgent botAgent = BotAgentManager.removeBot(existBot.getId());
            if(botAgent != null) {
                botAgent.shutdown();
                botAgent = null;
            }
        }

        String username = null;
        try {
            OkHttpClient client = null;
            if(botConfig.getUseProxy()) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1080));
                client = new OkHttpClient.Builder().proxy(proxy).build();
            } else {
                client = new OkHttpClient.Builder().build();
            }
            Request request = new Request.Builder()
                    .get()
                    .url("https://api.telegram.org/bot" + token + "/getMe")
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new JeecgBootException("Unexpected code " + response);
            }
            String info = response.body().string();
            JSONObject jsonInfo = JSON.parseObject(info);
            Boolean ok = jsonInfo.getBoolean("ok");
            if(ok==null || !ok) {
                TwoWayBot.instance().sendReplyMsg(chatId, replyMsgId, "❌ 机器人启动失败，请重试：token不合法，启动失败");
            }

            //添加机器人到平台
            JSONObject result = jsonInfo.getJSONObject("result");
            username = result.getString("username");
            String botId = result.getString("id");

            TgBot tgBot = botService.lambdaQuery().eq(TgBot::getBotName, username).one();
            boolean needInit = false;
            if(tgBot == null) {
                tgBot = new TgBot();
                tgBot.setBotToken(token);
                tgBot.setBotName(username);
                tgBot.setTgBotId(botId);
                tgBot.setAdminId(String.valueOf(update.getMessage().getFrom().getId()));
                tgBot.setAdminUsername(String.valueOf(update.getMessage().getFrom().getUserName()));
                botService.save(tgBot);

                //生成默认的欢迎语
                TgWelcomeConfig welcomeConfig = new TgWelcomeConfig();
                welcomeConfig.setConfigName("/start");
                welcomeConfig.setTextMsg(BotBizConstant.DEFAULT_WELCOME_TEXT);
                welcomeConfig.setBotId(tgBot.getId());
                welcomeConfigService.save(welcomeConfig);

                needInit = true;
            } else {
                BotAgent agent = BotAgentManager.queryById(tgBot.getId());
                if(agent == null) {
                    needInit = true;
                }
            }
            if(needInit) {
                BotAgent agent = BotAgent.create(token, username);
                agent.setDbBotId(tgBot.getId());
                agent.setTgBotId(tgBot.getTgBotId());
                BotAgentManager.addBot(tgBot.getId(), agent);
            }

            TwoWayBot.instance().sendMsgWithData(chatId, "✅ 添加机器人完成，机器人:@" + username, botService.twowayBotButton(userId), replyMsgId);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TwoWayBot.instance().sendReplyMsg(chatId, replyMsgId, "❌ 机器人启动失败");
        }
    }

}
