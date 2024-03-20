package org.jeecg.modules.telegram.handler.agent.msgtemplate;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgMsgTemplateService;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置消息模板"指令
 */
@Slf4j
@Service
public class SetMsgTemplateHandler implements BaseBotMsgHandler {

    @Resource
    private ITgMsgTemplateService msgTemplateService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "✍️ 点击按钮，进行相应消息模板设置。\n" +
                        "\n" +
                        "\uD83D\uDCAC 温馨提示：\n" +
                        "\n" +
                        "\uD83D\uDC49 发送/或者.加消息模板名字，将按照预先设定的内容回复。\n";
        Message message = update.getCallbackQuery().getMessage();
        MsgContentVO contentVO = msgTemplateService.tepmlateConfigMsg(botAgent.getDbBotId());
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
