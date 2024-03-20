package org.jeecg.modules.telegram.handler.agent.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理代理机器人的"进入网页版-聊天记录保存时长"指令
 */
@Slf4j
@Service
public class WebMsgSaveTimeHandler extends BaseBotDataHandler {

    @Resource
    private ITgBotService botService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        TgBot tgBot = botService.getById(botAgent.getDbBotId());
        Message message = update.getCallbackQuery().getMessage();
        String data = getCallbackData(update);
        String msg = "\uD83D\uDC49 请选择你需要设置的聊天记录保存时长:\n" +
                "\n" +
                "注：服务器每天零时会按照设置的时长清理聊天记录。" +
                "\n";

        if(StringUtils.isBlank(data)) {
            botAgent.sendMsgWithData(message.getChatId(), msg, buildBtnList(tgBot));
        } else {
            if(data.startsWith("SetWebMsgSaveTime_")) {
                String sel = data.substring("SetWebMsgSaveTime_".length());
                tgBot.setWebMsgSaveTime(Integer.parseInt(sel));
                botService.updateById(tgBot);
                botAgent.sendEditMsg(message.getChatId(), msg, buildBtnList(tgBot), message.getMessageId());
            }
        }
        callbackDataService.del(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + message.getChatId());
    }

    private ArrayList<List<InlineButtonVO>> buildBtnList(TgBot tgBot) {
        Class handlerClass = WebMsgSaveTimeHandler.class;
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        if(tgBot.getWebMsgSaveTime() == null) {
            tgBot.setWebMsgSaveTime(1);
        }
        return btnBuilder
                .addRow()
                .addDataButton((tgBot.getWebMsgSaveTime()==1 ? "✅" : "⬜️") + " 不保存", "SetWebMsgSaveTime_1", handlerClass)
                .addRow()
                .addDataButton((tgBot.getWebMsgSaveTime()==2 ? "✅" : "⬜️") + " 1天", "SetWebMsgSaveTime_2", handlerClass)
                .addDataButton((tgBot.getWebMsgSaveTime()==3 ? "✅" : "⬜️") + " 3天", "SetWebMsgSaveTime_3", handlerClass)
                .addDataButton((tgBot.getWebMsgSaveTime()==4 ? "✅" : "⬜️") + " 1周", "SetWebMsgSaveTime_4", handlerClass)
                .addRow()
                .addDataButton((tgBot.getWebMsgSaveTime()==5 ? "✅" : "⬜️") + " 1个月", "SetWebMsgSaveTime_5", handlerClass)
                .addDataButton((tgBot.getWebMsgSaveTime()==6 ? "✅" : "⬜️") + " 3个月", "SetWebMsgSaveTime_6", handlerClass)
                .addDataButton((tgBot.getWebMsgSaveTime()==7 ? "✅" : "⬜️") + " 半年", "SetWebMsgSaveTime_7", handlerClass)
                .addRow()
                .addDataButton((tgBot.getWebMsgSaveTime()==8 ? "✅" : "⬜️") + " 永久保存", "SetWebMsgSaveTime_8", handlerClass)
                .addRow()
                .addDataButton("返回", WebVerHandler.class)
                .build();
    }

}
