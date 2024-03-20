package org.jeecg.modules.telegram.handler.agent.autoreply;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置自动回复-仅管理员可触发"指令
 */
@Slf4j
@Service
public class SetAutoReplyOnlyForAdminHandler implements BaseBotMsgHandler {

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String configName = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgAutoReplyConfig config = autoReplyConfigService.getByConfigName(botAgent.getDbBotId(), configName);
        if(config != null) {
            if(config.getOnlyForAdmin() == 1) {
                config.setOnlyForAdmin(2);
            } else {
                config.setOnlyForAdmin(1);
            }
            autoReplyConfigService.updateById(config);
        }

        String keywordTips = BotUtil.autoReplyKeywordTips(config);
        String msg = "\uD83E\uDD16 自动回复 "+configName+" 设置，"+keywordTips+"\n" +
                        "\n" +
                        "✅ 切换自动回复触发权限完成";
        MsgContentVO contentVO = autoReplyConfigService.configItemMsg(config);
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
