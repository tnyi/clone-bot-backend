package org.jeecg.modules.telegram.handler.twoway.botmgr;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.bot.TwoWayBot;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * 处理"添加机器人"按钮点击事件
 */
@Slf4j
@Service
public class AddBotCallbackHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot baseBot, Update update) {
        log.info("提示添加机器人的操作方式");
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String msg = "\uD83D\uDC49 按照以下流程进行机器人添加：\n" +
                "\n" +
                "1. 打开 @BotFather\n" +
                "2. 发送 /newbot\n" +
                "3. 按指引设置机器人名字和username\n" +
                "4. 创建完成后将注册好的token发送给我\n" +
                "\n" +
                "token格式：\n" +
                "6422100000:AAFMTBWko3t7gA3mN5SRYp5FuYcxxxxxxxxx\n" +
                "\n" +
                "‼️ 警告！token不要在任何其他机器人上使用！";
        BotUtil.setTxtRelyHandler(baseBot.getDbBotId(), chatId, VerifyBotTokenHandler.class);
        List btnInfo = InlineButtonVO.builder().addRow().addDataButton("\uD83D\uDEAB 取消", CancelOperationHandler.class).build();
        TwoWayBot.instance().sendMsgWithData(chatId, msg, btnInfo);
    }

}
