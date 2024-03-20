package org.jeecg.modules.telegram.handler.agent.multiadmin;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBotAdmin;
import org.jeecg.modules.telegram.entity.TgMsgTemplate;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.agent.web.WebVerHandler;
import org.jeecg.modules.telegram.service.ITgBotAdminService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.service.ITgMsgTemplateService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理代理机器人的"设置管理员-删除"指令
 */
@Slf4j
@Service
public class DelAdminHandler implements BaseBotMsgHandler {

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgBotAdminService botAdminService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String configName = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgBotAdmin config = botAdminService.getById(configName);
        if(config != null) {
            botAdminService.removeById(config);
        }

        String msg = "✅ 删除管理员成功：" + config.getAdminId();
        List<TgBotAdmin> adminList = botAdminService.lambdaQuery().eq(TgBotAdmin::getBotId, botAgent.getDbBotId()).list();
        botAgent.sendEditMsg(message.getChatId(), msg, buildBtnList(adminList), message.getMessageId());
    }

    private ArrayList<List<InlineButtonVO>> buildBtnList(List<TgBotAdmin> adminList) {
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        btnBuilder = btnBuilder
                .addRow()
                .addDataButton("➕添加管理员", "AddMoreAdmin", AddAdminHandler.class);
        for(TgBotAdmin admin : adminList) {
            btnBuilder = btnBuilder
                    .addRow()
                    .addDataButton(admin.getAdminId()!=null ? admin.getAdminId() : admin.getAdminUsername(), null, null)
                    .addDataButton("\uD83D\uDDD1删除", admin.getId(), DelAdminHandler.class);
        }
        return
                btnBuilder.addRow()
                        .addDataButton("返回", BackToMainHandler.class)
                        .build();
    }

}
