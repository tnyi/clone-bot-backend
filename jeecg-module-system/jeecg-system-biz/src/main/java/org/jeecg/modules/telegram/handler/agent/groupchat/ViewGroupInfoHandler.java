package org.jeecg.modules.telegram.handler.agent.groupchat;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.twoway.TwoWayStartHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 查看群组消息
 */
@Slf4j
@Service
public class ViewGroupInfoHandler extends BaseBotDataHandler {

    @Resource
    private ITgChatService chatService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String groupChatId = getCallbackData(update);
        TgBot tgBot = botService.getById(botAgent.getDbBotId());

        GetChat getChat = GetChat.builder().chatId(groupChatId).build();
        Chat chatInfo = botAgent.execute(getChat);
        GetChatAdministrators getAdmin = GetChatAdministrators.builder().chatId(groupChatId).build();
        List<ChatMember> adminList = botAgent.execute(getAdmin);
        String msg = "\uD83D\uDC65群组\n\n" +
                "<b>群组id：</b> " + groupChatId +
                "\n<b>群组名字：</b> " + chatInfo.getTitle() +
                "\n<b>群组username：</b> " + (chatInfo.getUserName()==null?"":chatInfo.getUserName()) +
                "\n<b>群组管理员信息：</b> ";
        for(ChatMember chatMember : adminList) {
            User user = chatMember.getUser();
            String nickName = (user.getFirstName()==null ? "" : user.getFirstName()) + (user.getLastName()==null ? "" : user.getLastName());
            String icon = "\uD83D\uDC6E";
            if(user.getId().toString().equals(tgBot.getTgBotId())) {
                icon = "\uD83E\uDD16";
            }
            msg = msg + "\n" + icon + "<b><a href=\"https://t.me/"+user.getUserName()+"\">" + nickName + "</a></b> - " + user.getId() + " - <a href=\"tg://user?id=" + user.getId() + "\">@" + user.getUserName() + "</a>";
        }
        TgChat tgChat = chatService.lambdaQuery().eq(TgChat::getBotId, botAgent.getDbBotId()).eq(TgChat::getChatId, groupChatId).one();
        String status = tgChat.getStatus() == 1 ? "✅ 可使用" : "❌ 已禁用";
        msg = msg + "\n\n<b>状态：</b>" + status;
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        if(tgChat.getStatus() == 1) {
            btnBuilder.addRow().addDataButton("❌禁止使用", groupChatId, BanGroupHandler.class);
        } else {
            btnBuilder.addRow().addDataButton("✅允许使用", groupChatId, UnbanGroupHandler.class);
        }
        btnBuilder.addRow().addDataButton("返回", GroupChatListHandler.class);
        Object editType = redisUtil.get(BotRedisConstant.GROUP_CHAT_EDIT_TYPE + chatId);
        if(editType != null) {
            botAgent.sendEditMsg(chatId, msg, btnBuilder.build(), message.getMessageId());
        } else {
            botAgent.sendMsgWithData(chatId, msg, btnBuilder.build());
        }
    }

}
