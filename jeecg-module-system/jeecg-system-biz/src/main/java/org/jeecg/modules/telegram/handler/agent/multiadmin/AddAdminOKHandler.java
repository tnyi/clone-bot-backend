package org.jeecg.modules.telegram.handler.agent.multiadmin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgBotAdmin;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.entity.TgMsgTemplate;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgBotAdminService;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.service.ITgMsgTemplateService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置多管理员-添加管理员-成功"指令
 */
@Slf4j
@Service
public class AddAdminOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotAdminService botAdminService;

    @Resource
    private SetMultiAdminHandler setMultiAdminHandler;

    @Resource
    private ITgBotService tgBotService;

    @Resource
    private ITgChatService chatService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        String botId = botAgent.getDbBotId();
        TgBot tgBot = tgBotService.getById(botId);

        String configName = message.getText();
        String adminId = configName;
        String adminName = "";
        String adminNickname = "";
        String adminChatId = "";
        if(configName.startsWith("@")) {
            configName = configName.substring(1);
            adminName = configName;
        }
        boolean exist = tgBot.getAdminId().equals(adminId) || configName.equals(tgBot.getAdminUsername());
        if(exist) {
            String msg = "⚠️<a href=\"tg://user?id="+adminId+"\">"+tgBot.getAdminUsername()+"</a> 已经是管理员，请勿重复添加！";
            botAgent.sendMsg(message.getChatId(), msg);
            return;
        }

        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        if(message.getReplyToMessage() != null) {
            configName = String.valueOf(message.getReplyToMessage().getFrom().getId());
            adminId = configName;
            adminName = message.getReplyToMessage().getFrom().getUserName();
            adminNickname = BotUtil.getFullname(message.getReplyToMessage().getFrom());
        }
        if(message.getForwardFrom() != null) {
            configName = String.valueOf(message.getForwardFrom().getId());
            adminId = configName;
            adminName = message.getForwardFrom().getUserName();
            adminNickname = BotUtil.getFullname(message.getForwardFrom());
        }

        final String queryAdminId = adminId;
        final String queryAdminName = adminName;
        TgChat existChat = chatService.lambdaQuery().eq(TgChat::getBotId, botAgent.getDbBotId())
                .and(i -> i.eq(TgChat::getAdminId, queryAdminId).or().eq(TgChat::getAdminUsername, queryAdminName))
                .last("limit 1").one();
        if(existChat == null) {
            String msg = "⚠️该账号不是系统用户，无法设置为管理员！";
            botAgent.sendMsg(message.getChatId(), msg);
            return;
        }
        adminChatId = existChat.getChatId();

        if(StringUtils.isBlank(adminNickname)) {
            GetChatMember getChatMember = GetChatMember.builder().chatId(existChat.getChatId()).userId(Long.parseLong(existChat.getAdminId())).build();
            ChatMember chatMember = botAgent.execute(getChatMember);
            adminNickname = BotUtil.getFullname(chatMember.getUser());
        }

        String msg = "✅ 添加管理员成功：" + configName;
        final String searchKey = configName;
        long existAdmin = botAdminService.lambdaQuery()
                            .eq(TgBotAdmin::getBotId, botId)
                            .and(wp -> wp.eq(TgBotAdmin::getAdminId, searchKey).or().eq(TgBotAdmin::getAdminUsername, searchKey))
                            .count();
        if(existAdmin > 0) {
            msg = "管理员已经添加，无需重复添加：" + configName;
        } else {
            TgBotAdmin admin = new TgBotAdmin();
            admin.setAdminId(adminId);
            admin.setAdminUsername(adminName);
            admin.setBotId(botId);
            admin.setAdminNickname(adminNickname);
            admin.setChatId(adminChatId);
            botAdminService.save(admin);
        }
        botAgent.sendMsg(message.getChatId(), msg);

        setMultiAdminHandler.process(botAgent, update);
    }

}
