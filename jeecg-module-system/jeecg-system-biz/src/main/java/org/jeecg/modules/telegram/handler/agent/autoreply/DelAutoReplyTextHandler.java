package org.jeecg.modules.telegram.handler.agent.autoreply;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置自动回复-文本-删除"指令
 */
@Slf4j
@Service
public class DelAutoReplyTextHandler extends BaseBotDataHandler {

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();
        String configName = getCallbackData(update);
        TgAutoReplyConfig config = autoReplyConfigService.getByConfigName(botAgent.getDbBotId(), configName);
        if(config != null) {
            config.setTextMsg(null);
            autoReplyConfigService.lambdaUpdate().set(TgAutoReplyConfig::getTextMsg, null).eq(TgAutoReplyConfig::getId, config.getId()).update();
        }

        String keywordTips = BotUtil.autoReplyKeywordTips(config);
        String msg = "\uD83E\uDD16 自动回复 <b>"+configName+"</b> 设置，"+keywordTips+"\n" +
                        "\n" +
                        "✅ 回复文本已经删除";
        MsgContentVO contentVO = autoReplyConfigService.configItemMsg(config);
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
