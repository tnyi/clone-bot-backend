package org.jeecg.modules.telegram.handler.agent;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.*;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * 处理代理机器人的"Html标签帮助"指令
 */
@Slf4j
@Service
public class HtmlTagHelpHandler implements BaseBotMsgHandler {

    @Override
    public void process(BaseBot botAgent, Update update) {
        String msg = "\uD83D\uDC49 <b>下面是Html标签演示和说明：</b>\n" +
                        "        \n" +
                        "<b>加粗演示</b>  &lt;b&gt;加粗演示&lt;/b&gt;\n" +
                        "<i>斜体演示</i>  &lt;i&gt;斜体演示&lt;/i&gt;\n" +
                        "<u>下划线演示</u>  &lt;u&gt;下划线演示&lt;/u&gt;\n" +
                        "<s>删除线演示</s>  &lt;s&gt;删除线演示&lt;/s&gt;\n" +
                        "<b><i>加粗和斜体</i></b>  &lt;b&gt;&lt;i&gt;加粗和斜体&lt;/i&gt;&lt;/b&gt;\n" +
                        "<u><i>下划线和斜体</i></u>  &lt;u&gt;&lt;i&gt;下划线和斜体&lt;/i&gt;&lt;/u&gt;\n" +
                        "<a href=\"http://t.me/powell_two_way_bot\">链接跳转</a>  &lt;a href=\"http://t.me/powell_two_way_bot\">链接跳转&lt;/a&gt;\n" +
                        "<a href=\"tg://user?id=6808297712\">点击显示用户</a>  &lt;a href=\"tg://user?id=6808297712\"&gt;点击显示用户&lt;/a&gt;\n" +
                        "<code>点击可复制的文本</code>  &lt;code&gt;点击可复制的文本&lt;/code>\n" +
                        "<pre>这里是文本块演示</pre>" +
                        "  &lt;pre&gt;这里是文本块演示&lt;/pre&gt;";

        Message message = update.getCallbackQuery().getMessage();
        botAgent.sendMsg(message.getChatId(), msg);
    }

}
