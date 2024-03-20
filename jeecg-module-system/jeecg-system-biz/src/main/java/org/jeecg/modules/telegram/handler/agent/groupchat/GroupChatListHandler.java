package org.jeecg.modules.telegram.handler.agent.groupchat;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.entity.TgMsgTemplate;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.agent.msgtemplate.AddMsgTemplateHandler;
import org.jeecg.modules.telegram.handler.agent.msgtemplate.ConfigMsgTemplateItemHandler;
import org.jeecg.modules.telegram.handler.agent.msgtemplate.DelMsgTemplateHandler;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * 处理代理机器人的"我的群组"指令
 */
@Slf4j
@Service
public class GroupChatListHandler extends BaseBotDataHandler {

    @Resource
    private ITgChatService tgChatService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();

        List<TgChat> tgChatList = tgChatService.lambdaQuery().eq(TgChat::getBotId, botAgent.getDbBotId()).eq(TgChat::getChatType, 2).list();
        for(TgChat chat : tgChatList) {
            GetChat getChat = GetChat.builder().chatId(chat.getChatId()).build();
            Chat chatInfo = botAgent.execute(getChat);
            btnBuilder = btnBuilder.addRow()
                    .addDataButton("✅ " + chatInfo.getTitle(), chat.getChatId(), ViewGroupInfoHandler.class, false);
        }
        btnBuilder.addRow().addDataButton("返回", BackToMainHandler.class, true);
        String msg = "\uD83D\uDC65我的群组\n\n" + "点击对应群组按钮可以查看群组信息";
        botAgent.sendMsgWithData(message.getChatId(), msg, btnBuilder.build());
    }

}
