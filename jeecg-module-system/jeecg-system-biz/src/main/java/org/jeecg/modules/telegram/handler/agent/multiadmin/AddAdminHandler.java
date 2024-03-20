package org.jeecg.modules.telegram.handler.agent.multiadmin;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgBotAdmin;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgBotAdminService;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置多管理员-添加管理员"指令
 */
@Slf4j
@Service
public class AddAdminHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotService botService;

    @Resource
    private ITgBotAdminService botAdminService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        TgBot tgBot = botService.getById(botAgent.getDbBotId());
        long existAdmin = botAdminService.lambdaQuery()
                            .eq(TgBotAdmin::getBotId, botAgent.getDbBotId())
                            .count();
        if(tgBot.getServiceType() == 1 && existAdmin >= 3) {
            String msg = "⚠️ 免费版最多设置3个管理员，请开通专业版！";
            BotUtil.upgradeTips(botAgent, message.getChatId(), msg);
            return;
        }

        String msg = "\uD83D\uDC49 请使用以下其中一种方式添加管理员：\n" +
                        "\n" +
                        "• 发送<b>用户id</b>给我\n" +
                        "• 发送用户名给我，格式为<b>@username</b>\n" +
                        "• 使用 /admin 回复一条用户消息\n" +
                        "• 转发一条用户消息给我";
        BotUtil.setTxtRelyHandler(botAgent.getDbBotId(),  message.getChatId(), AddAdminOKHandler.class);
        botAgent.sendMsg(message.getChatId(), msg);
    }

}
