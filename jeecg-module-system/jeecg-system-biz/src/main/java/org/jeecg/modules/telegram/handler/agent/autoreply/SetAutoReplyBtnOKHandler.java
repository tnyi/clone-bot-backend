package org.jeecg.modules.telegram.handler.agent.autoreply;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置自动回复-设置按钮成功"指令
 */
@Slf4j
@Service
public class SetAutoReplyBtnOKHandler extends BaseBotDataHandler {

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Resource
    private ITgBotService botService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        BotUtil.setNotNeedSend(update);
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());

        TgBot tgBot = botService.getById(botAgent.getDbBotId());
        int total = StringUtils.countMatches(message.getText(), "\n") + StringUtils.countMatches(message.getText(), "|");
        if(tgBot.getServiceType() == 1 && total >= 5) {
            String msg = "⚠️ 免费版最多设置5个消息按钮，请开通专业版！";
            BotUtil.upgradeTips(botAgent, message.getChatId(), msg);
            return;
        }

        String configName = getBizData(chatId);
        TgAutoReplyConfig replyConfig = autoReplyConfigService.getByConfigName(botAgent.getDbBotId(), configName);
        replyConfig.setMsgButton(message.getText());
        autoReplyConfigService.updateById(replyConfig);

        String keywordTips = BotUtil.autoReplyKeywordTips(replyConfig);
        String msg = "\uD83E\uDD16 自动回复 "+configName+" 设置，"+keywordTips+"\n" +
                        "\n" +
                        "✅ 设置消息按钮成功";
        MsgContentVO contentVO = autoReplyConfigService.configItemMsg(replyConfig);
        botAgent.sendMsgWithData(chatId, msg, contentVO.getButtonList());
    }

}
