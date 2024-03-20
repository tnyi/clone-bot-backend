package org.jeecg.modules.telegram.handler.agent.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.config.WebIMConfig;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.twoway.upgrade.StartUpgradeBotHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理代理机器人的"进入网页版"以及重置登录链接，设置网页版消息是否通知管理员的指令
 */
@Slf4j
@Service
public class WebVerHandler extends BaseBotDataHandler {

    @Resource
    private ITgBotService botService;

    @Resource
    private WebIMConfig webIMConfig;

    @Override
    public void process(BaseBot botAgent, Update update) {
        TgBot tgBot = botService.getById(botAgent.getDbBotId());
        Message message = update.getCallbackQuery().getMessage();
        if(tgBot.getServiceType() == 1) {
            String msg = "⚠️ 当前为<b>免费版</b>，请升级专业版后再使用该功能！";
            BotUtil.upgradeTips(botAgent, message.getChatId(), msg);
            return;
        }

        String data = getCallbackData(update);
        String token = StringUtils.isNotBlank(tgBot.getWebLoginToken()) ? tgBot.getWebLoginToken() : UUIDGenerator.generate();
        String msg = "\uD83C\uDF10 欢迎使用网页版，当前可同时登陆设备：3。\n" +
                "\n" +
                "复制下面链接，到电脑或者手机浏览器中打开，授权后即可登录。\n" +
                "\n" +
                webIMConfig.getFrontUrl() + "?token=" + token + "\n" +
                "\n" +
                "有任何问题，欢迎进入交流群交流。";
        if(StringUtils.isBlank(data)) {
            if(!token.equals(tgBot.getWebLoginToken())) {
                tgBot.setWebLoginToken(token);
                botService.updateById(tgBot);
            }
            botAgent.sendMsgWithData(message.getChatId(), msg, buildBtnList(tgBot));
        }
        else {
            if ("ResetLoginUrl".equals(data)) {
                token = UUIDGenerator.generate();
                msg = "\uD83C\uDF10 刷新成功!\n" +
                        "\n" +
                        "复制下面链接，到电脑或者手机浏览器中打开，授权后即可登录。\n" +
                        "\n" +
                        webIMConfig.getFrontUrl() + "?token=" + token;
                tgBot.setWebLoginToken(token);
                botService.updateById(tgBot);
                botAgent.sendEditMsg(message.getChatId(), msg, buildBtnList(tgBot), message.getMessageId());
            }
            if ("WebMsgNotifyAdmin".equals(data)) {
                Integer flag = tgBot.getWebMsgNotifyAdmin();
                flag = (flag == null || flag == 2) ? 1 : 2;
                tgBot.setWebMsgNotifyAdmin(flag);
                botService.updateById(tgBot);
                botAgent.sendEditMsg(message.getChatId(), msg, buildBtnList(tgBot), message.getMessageId());
            }
        }
    }

    private ArrayList<List<InlineButtonVO>> buildBtnList(TgBot tgBot) {
        Class handlerClass = WebVerHandler.class;
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        Integer saveTime = tgBot.getWebMsgSaveTime();
        String[] descArr = {"不保存", "1天", "3天", "1周", "1个月", "3个月", "半年", "永久保存"};
        String saveTimeDesc = descArr[saveTime-1];
        return btnBuilder
                .addRow()
                .addDataButton("\uD83D\uDD04重置登录链接", "ResetLoginUrl", handlerClass)
                .addDataButton("\uD83D\uDCBB管理登录设备", WebDeviceMgrHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDCDD聊天记录保存时长: " + saveTimeDesc, WebMsgSaveTimeHandler.class)
                .addRow()
                .addDataButton(((tgBot.getWebMsgNotifyAdmin()!=null && tgBot.getWebMsgNotifyAdmin()==1) ? "✅" : "⬜️") + "网页端发送消息通知我", "WebMsgNotifyAdmin", handlerClass)
                .addRow()
                .addDataButton("返回", BackToMainHandler.class)
                .build();
    }

}
