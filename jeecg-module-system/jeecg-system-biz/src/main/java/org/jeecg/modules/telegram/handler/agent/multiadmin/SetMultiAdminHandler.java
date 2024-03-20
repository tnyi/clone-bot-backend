package org.jeecg.modules.telegram.handler.agent.multiadmin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgBotAdmin;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.agent.web.WebVerHandler;
import org.jeecg.modules.telegram.service.ITgBotAdminService;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理代理机器人的"设置多管理员"指令
 */
@Slf4j
@Service
public class SetMultiAdminHandler implements BaseBotMsgHandler {

    @Resource
    private ITgBotAdminService botAdminService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        List<TgBotAdmin> adminList = botAdminService.lambdaQuery().eq(TgBotAdmin::getBotId, botAgent.getDbBotId()).list();
        String msg = "\uD83D\uDC6E️ 多管理员设置。\n" +
                        "\n" +
                        "注：点击对应管理员按钮可以进行权限管理。";
        Long chatId = null;
        if(update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();
            chatId = message.getChatId();
        } else {
            chatId = update.getMessage().getChatId();
        }
        botAgent.sendMsgWithData(chatId, msg, buildBtnList(adminList));
    }

    private ArrayList<List<InlineButtonVO>> buildBtnList(List<TgBotAdmin> adminList) {
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        btnBuilder.addRow().addDataButton("➕添加管理员", "AddMoreAdmin", AddAdminHandler.class);
        for(TgBotAdmin admin : adminList) {
            btnBuilder.addRow()
                    .addDataButton("\uD83D\uDC6E️" + (StringUtils.isNotBlank(admin.getAdminNickname()) ? admin.getAdminNickname() : admin.getAdminId()), null, null)
                    .addDataButton("\uD83D\uDDD1删除", admin.getId(), DelAdminHandler.class);
        }
        return btnBuilder.addRow().addDataButton("返回", BackToMainHandler.class).build();
    }

}
