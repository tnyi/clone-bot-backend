package org.jeecg.modules.telegram.handler.agent.autoreply;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"设置自动回复"指令
 */
@Slf4j
@Service
public class SetAutoReplyHandler implements BaseBotMsgHandler {

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "\uD83E\uDD16 点击按钮，进行相应自动回复设置。\n" +
                "\n" +
                "\uD83D\uDCAC 温馨提示：\n" +
                "\n" +
                "\uD83D\uDC49 中间列为图标显示，具体含义点击可以查看。⚠️表示未生效。\n" +
                "\n" +
                "\uD83D\uDC49 每条自动回复可设置多个关键词。\n" +
                "\n" +
                "\uD83D\uDC49 两种匹配方式：\n" +
                "1.精确模式：发送的内容跟关键词完全相等。\n" +
                "2.模糊模式：发送的内容存在关键词。\n" +
                "\n" +
                "<i>注：群关键词回复需要在个性化设置中开启，给机器人管理员权限</i>";
        Message message = update.getCallbackQuery().getMessage();
        MsgContentVO contentVO = autoReplyConfigService.autoReplyConfigMsg(botAgent.getDbBotId());
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
