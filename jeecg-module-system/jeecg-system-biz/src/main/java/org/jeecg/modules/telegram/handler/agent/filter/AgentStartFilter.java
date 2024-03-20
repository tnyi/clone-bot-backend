package org.jeecg.modules.telegram.handler.agent.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.*;
import org.jeecg.modules.telegram.handler.BotMsgFilter;
import org.jeecg.modules.telegram.handler.BotMsgFilterChain;
import org.jeecg.modules.telegram.service.*;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 处理代理机器人的"开始"指令
 */
@Slf4j
@Service
public class AgentStartFilter implements BotMsgFilter {

    @Resource
    private ITgChatService tgChatService;

    @Resource
    private ITgBotService tgBotService;

    @Resource
    private ITgWelcomeConfigService welcomeConfigService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgUserContactService userContactService;

    @Resource
    private ITgBlacklistService blacklistService;

    @Override
    public void doFilter(BaseBot botAgent, Update update, BotMsgFilterChain filterChain) {
        Message message = update.getMessage();
        String txt = message.getText();
        if(!"/start".equals(txt)) {
            filterChain.doFilter(botAgent, update);
            return;
        }

        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        String username = message.getFrom().getUserName();
        String nickname = BotUtil.getFullname(message.getFrom());
        TgBot tgBot = tgBotService.getById(botAgent.getDbBotId());

        String groupId = "";
        String groupName = "";
        if(message.isGroupMessage()) {
            GetChat getChat = GetChat.builder().chatId(chatId).build();
            Chat chatInfo = botAgent.execute(getChat);
            groupId = chatInfo.getId().toString();
            groupName = chatInfo.getTitle();
        }

        if(!userId.toString().equals(tgBot.getAdminId())) {
            //普通用户显示欢迎语
            List<TgWelcomeConfig> configList = welcomeConfigService.lambdaQuery()
                                                        .eq(TgWelcomeConfig::getConfigType, 1)
                                                        .isNotNull(TgWelcomeConfig::getTextMsg)
                                                        .eq(TgWelcomeConfig::getBotId, tgBot.getId()).list();
            for(TgWelcomeConfig config : configList) {
                if(StringUtils.isBlank(config.getTextMsg())) {
                    //未生效的
                    continue;
                }
                String msg = BotUtil.checkAdTail(tgBot, config.getTextMsg());
                msg = msg.replace("{id}", userId.toString());
                msg = msg.replace("{id_code}", "<code>" + userId + "</code>");
                msg = msg.replace("{display_name}", nickname);
                msg = msg.replace("{mention}", "<a href=\"tg://user?id=" + userId + "\">" + nickname + "</a>");
                msg = msg.replace("{username}", username);
                msg = msg.replace("{group_id}", groupId);
                msg = msg.replace("{group_id_code}", "<code>" + groupId + "</code>");
                msg = msg.replace("{group_name}", groupName);

                if(config.getButtonType() == null || config.getButtonType() == 1) {
                    BotUtil.sendTextMsg(botAgent, chatId, config.getMediaType(), msg, config.getMsgButton(), config.getMediaFile());
                } else {
                    Set<KeyboardRow> keyboardRowSet = BotUtil.parseBottomBtn(config.getMsgButton());
                    ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder().keyboard(keyboardRowSet).resizeKeyboard(true).build();
                    BotUtil.sendTextMsg(botAgent, chatId, config.getMediaType(), msg, "", config.getMediaFile(), null, replyKeyboardMarkup);
                }
            }

            //更新回复次数
            tgBot.setReplyCount(tgBot.getReplyCount() + 1);
            tgBotService.updateById(tgBot);

            //判断是否开启了欢迎语触发提示
            if(tgBot.getWelcomeMsgTips() == 1) {
                TgChat adminChat = tgChatService.lambdaQuery()
                        .eq(TgChat::getBotId, botAgent.getDbBotId())
                        .eq(TgChat::getChatType, 1)
                        .eq(TgChat::getAdminChat, 1)
                        .last("limit 1").one();
                String tips = "\uD83D\uDCAC <b><a href=\"tg://user?id=" + userId + "\">" + nickname + "</a></b> 发送的 [<b>/start</b>]，触发了欢迎语。\n" +
                        "\n" +
                        "<i>(该提示消息可在 个性化设置-->欢迎语的触发提示 中关闭)</i>";
                MsgContentVO msgContentVO = blacklistService.banUserMsg(botAgent, message, tgBot);
                botAgent.sendMsgWithData(Long.parseLong(adminChat.getChatId()), tips, msgContentVO.getButtonList());

                //更新管理员的最近联系人列表
                TgUserContact userContact = userContactService.lambdaQuery()
                        .eq(TgUserContact::getUserId, adminChat.getAdminId())
                        .eq(TgUserContact::getContactTgId, userId)
                        .eq(TgUserContact::getBotId, botAgent.getDbBotId()).one();
                if(userContact == null) {
                    userContact = new TgUserContact();
                    userContact.setUserId(adminChat.getAdminId());
                    userContact.setContactTgId(String.valueOf(userId));
                    userContact.setContactTgUsername(username);
                    userContact.setBotId(botAgent.getDbBotId());
                    userContactService.save(userContact);
                } else {
                    userContact.setUpdateTime(new Date());
                    userContactService.updateById(userContact);
                }
            }

            filterChain.disableForward();
            filterChain.doFilter(botAgent, update);
            return;
        }

        //只有管理员与机器人创建的对话才可以看到管理菜单
        TgChat existChat = tgChatService.lambdaQuery()
                .eq(TgChat::getChatId, chatId)
                .eq(TgChat::getBotId, botAgent.getDbBotId()).last("limit 1").one();
        if(existChat == null) {
            String chatType = update.getMessage().getChat().getType();
            TgChat tgChat = new TgChat();
            tgChat.setChatId(String.valueOf(chatId));
            tgChat.setBotId(botAgent.getDbBotId());
            tgChat.setChatType("private".equals(chatType) ? 1 : 2);
            tgChat.setAdminChat(tgBot.getAdminId().equals(userId.toString()) ? 1 : 2);
            tgChat.setAdminId(String.valueOf(userId));
            tgChat.setBotIsAdmin(1);
            tgChat.setStatus(1);
            tgChat.setAdminUsername(username);
            tgChatService.save(tgChat);
            //同步账号到web im
            tgChatService.registerUserToWebIM(botAgent, tgChat);
        }

        callbackDataService.del(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        MsgContentVO mainMenu = BotUtil.createAgentMenu(tgBot);
        botAgent.sendMsgWithData(update.getMessage().getChatId(), mainMenu.getText(), mainMenu.getButtonList());

        filterChain.disableForward();
        filterChain.doFilter(botAgent, update);
    }

}
