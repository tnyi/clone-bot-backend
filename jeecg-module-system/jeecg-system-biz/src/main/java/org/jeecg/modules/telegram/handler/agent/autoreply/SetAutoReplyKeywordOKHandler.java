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
 * 处理代理机器人的"设置自动回复-某条目-设置关键词-成功"指令
 */
@Slf4j
@Service
public class SetAutoReplyKeywordOKHandler extends BaseBotDataHandler {

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String configName = getBizData(chatId);
        TgAutoReplyConfig replyConfig = autoReplyConfigService.getByConfigName(botAgent.getDbBotId(), configName);
        replyConfig.setKeywordList(message.getText());
        autoReplyConfigService.updateById(replyConfig);

        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        String keywordTips = BotUtil.autoReplyKeywordTips(replyConfig);
        String msg = "\uD83E\uDD16 自动回复 "+configName+" 设置，" + keywordTips + "\n" +
                        "\n" +
                        "✅ 关键词组设置成功";
        MsgContentVO contentVO = autoReplyConfigService.configItemMsg(replyConfig);
        botAgent.sendMsgWithData(chatId, msg, contentVO.getButtonList());
    }

}
