package org.jeecg.modules.telegram.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.SpringContextHolder;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotBizConstant;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgMsgTemplate;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.agent.autoreply.SetAutoReplyHandler;
import org.jeecg.modules.telegram.handler.agent.blacklist.SetBlacklistHandler;
import org.jeecg.modules.telegram.handler.agent.botinfo.EditAvatarHandler;
import org.jeecg.modules.telegram.handler.agent.botinfo.EditBotNameHandler;
import org.jeecg.modules.telegram.handler.agent.bottombtn.DelBottomButtonHandler;
import org.jeecg.modules.telegram.handler.agent.bottombtn.SetBottomButtonHandler;
import org.jeecg.modules.telegram.handler.agent.bottombtn.SetBottomButtonTextHandler;
import org.jeecg.modules.telegram.handler.agent.broadcast.SetBroadcastTextHandler;
import org.jeecg.modules.telegram.handler.agent.command.DelCommandHandler;
import org.jeecg.modules.telegram.handler.agent.command.SetCommandHandler;
import org.jeecg.modules.telegram.handler.agent.command.SetCommandTextHandler;
import org.jeecg.modules.telegram.handler.agent.groupchat.GroupChatListHandler;
import org.jeecg.modules.telegram.handler.agent.groupwelcome.GroupWelcomeListHandler;
import org.jeecg.modules.telegram.handler.agent.groupwelcome.SetGroupWelcomeHandler;
import org.jeecg.modules.telegram.handler.agent.msgtemplate.SetMsgTemplateHandler;
import org.jeecg.modules.telegram.handler.agent.multiadmin.SetMultiAdminHandler;
import org.jeecg.modules.telegram.handler.agent.personal.PersonalConfigHandler;
import org.jeecg.modules.telegram.handler.agent.stat.ShowStatHandler;
import org.jeecg.modules.telegram.handler.agent.web.WebVerHandler;
import org.jeecg.modules.telegram.handler.agent.welcome.WelcomeListHandler;
import org.jeecg.modules.telegram.handler.twoway.upgrade.UpgradeBotHandler;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.jeecg.modules.telegram.vo.TxtMsgHandlerVO;
import org.springframework.beans.BeansException;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * 机器人相关工具方法
 */
@Slf4j
public class BotUtil {

    private static ITgCallbackDataService callbackDataService = null;

    /**文件保存路径*/
    public static String uploadpath = null;

    /**广告尾巴链接*/
    public static String techSupportUrl = null;

    /**广告尾巴技术支持人*/
    public static String techSupportUsername = null;

    /**是否使用代理访问 tg*/
    public static Boolean useProxy = false;

    /**
     * 机器人默认参数
     * @return
     */
    public static DefaultBotOptions createBotOption() {
        DefaultBotOptions botOptions = new DefaultBotOptions();
        if(useProxy) {
            botOptions.setProxyHost("127.0.0.1");
            botOptions.setProxyPort(1080);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);
        }
        return botOptions;
    }

    /**
     * 初始化机器人，添加开始菜单
     * @param bot
     */
    public static BotSession initBot(TelegramLongPollingBot bot) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            BotSession session = telegramBotsApi.registerBot(bot);

//            BotCommand botCommand = new BotCommand();
//            botCommand.setCommand("/start");
//            botCommand.setDescription("start bot");
//            SetMyCommands setMyCommands = SetMyCommands.builder().command(botCommand).build();
//            bot.execute(setMyCommands);

            return session;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JeecgBootException("初始化机器人异常," + e.getMessage());
        }
    }

    /**
     * 创建代理主界面提示语和菜单按钮
     * @return
     */
    public static MsgContentVO createAgentMenu(TgBot bot) {
        String expireStr = "";
        if(bot.getServiceType()==2) {
            if((bot.getExpireTime().getTime() - System.currentTimeMillis()) > BotBizConstant.MAX_EXPIRE_TIME){
                expireStr = "✅永久使用";
            } else {
                if(bot.getExpireTime() == null) {
                    expireStr = "免费版";
                } else {
                    expireStr = DateUtils.formatDate(bot.getExpireTime());
                }
            }
        }
        String msg = "\uD83C\uDFE0 你好，管理者！\n" +
                "\n" +
                "当前版本："+(bot.getServiceType()==1 ? "<b>免费版</b>" : "<b>专业版</b> (到期时间 " + expireStr + ")")+"\n" +
                "\n" +
                "点击下方按钮可以进入对应功能。";

        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        btnBuilder = btnBuilder
                        .addRow()
                        .addDataButton("✍️修改名字", EditBotNameHandler.class, false)
                        .addDataButton("✍️修改头像", EditAvatarHandler.class, false)
                        .addRow()
                        .addDataButton("\uD83C\uDF89设置欢迎语", WelcomeListHandler.class, true)
                        .addDataButton("\uD83C\uDF8A设置进群欢迎", GroupWelcomeListHandler.class, true)
                        .addRow()
                        .addDataButton("\uD83E\uDD16设置自动回复", SetAutoReplyHandler.class, true)
                        .addDataButton("\uD83D\uDD79设置底部按钮", SetBottomButtonHandler.class, true)
                        .addRow()
                        .addDataButton("✍️设置消息模板", SetMsgTemplateHandler.class, true)
                        .addDataButton("\uD83D\uDCA1设置指令", SetCommandHandler.class, true)
                        .addRow()
                        .addDataButton("❌黑名单", SetBlacklistHandler.class, true)
                        .addDataButton("\uD83D\uDCE1广播", SetBroadcastTextHandler.class, true)
                        .addRow()
                        .addDataButton("\uD83D\uDCCA统计", ShowStatHandler.class, true)
                        .addDataButton("⚙️个性化设置", PersonalConfigHandler.class, true)
                        .addRow()
                        .addDataButton("\uD83D\uDC6E多管理员", SetMultiAdminHandler.class, true)
                        .addDataButton("\uD83C\uDF10进入网页版", WebVerHandler.class, true)
                        .addRow()
                        .addDataButton("\uD83D\uDC65我的群组", GroupChatListHandler.class, true);
        ArrayList<List<InlineButtonVO>> buttonList = btnBuilder.build();
        MsgContentVO msgContentVO = new MsgContentVO();
        msgContentVO.setText(msg);
        msgContentVO.setButtonList(buttonList);
        return msgContentVO;
    }

    /**
     * 获取类实例
     * @param var1
     * @return
     * @param <T>
     * @throws BeansException
     */
    public static <T> T getBean(Class<T> var1) throws BeansException {
        return SpringContextHolder.getApplicationContext().getBean(var1);
    }

    /**
     * 根据配置显示关键词的提示语
     * @param replyConfig
     * @return
     */
    public static String autoReplyKeywordTips(TgAutoReplyConfig replyConfig) {
        String keywordTips = StringUtils.isNotBlank(replyConfig.getKeywordList()) ? ("当前关键词：" + replyConfig.getKeywordList()) : "当前并未设置关键词";
        return keywordTips;
    }

    /**
     * 根据消息模板提示语
     * @param replyConfig
     * @return
     */
    public static String msgTemplateTips(TgMsgTemplate replyConfig) {
        String tips = "✍️消息模板 <b>"+replyConfig.getTemplateName()+"</b>设置，通过发送 /"+replyConfig.getTemplateName()+" 或者 ."+replyConfig.getTemplateName()+" 触发。";
        return tips;
    }

    /**
     * 设置底部按钮的界面
     * @return
     */
    public static ArrayList<List<InlineButtonVO>> configBottomButton() {
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder()
                .addRow()
                .addDataButton("⚙️设置", SetBottomButtonTextHandler.class)
                .addDataButton("\uD83D\uDDD1清空", DelBottomButtonHandler.class)
                .addRow()
                .addDataButton("返回", BackToMainHandler.class);
        return btnBuilder.build();
    }

    /**
     * 设置指令的界面
     * @return
     */
    public static ArrayList<List<InlineButtonVO>> configCommand() {
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder()
                .addRow()
                .addDataButton("⚙️设置", SetCommandTextHandler.class)
                .addDataButton("\uD83D\uDDD1还原", DelCommandHandler.class)
                .addRow()
                .addDataButton("返回", BackToMainHandler.class, true);
        return btnBuilder.build();
    }

    /**
     * 配置特殊业务文本消息的处理类
     * @param botId
     * @param chatId
     * @param handler
     */
    public static void setTxtRelyHandler(String botId, Long chatId, Class handler) {
        TxtMsgHandlerVO handlerVO = new TxtMsgHandlerVO(handler);
        getCallbackDataService().set(BotRedisConstant.BIZ_REPLY_CHAT_ID + botId + ":" + chatId, handlerVO);
    }

    /**
     * 移除特殊业务文本消息的处理类
     * @param botId
     * @param chatId
     */
    public static void delTxtRelyHandler(String botId, Long chatId) {
        getCallbackDataService().del(BotRedisConstant.BIZ_REPLY_CHAT_ID + botId + ":" + chatId);
    }

    /**
     * 获取当前会话的特殊业务文本处理类
     * @param botId
     * @param chatId
     * @return
     */
    public static TxtMsgHandlerVO getTxtReplyHandler(String botId, Long chatId) {
        return getCallbackDataService().getTxtMsgHandler(BotRedisConstant.BIZ_REPLY_CHAT_ID + botId + ":" + chatId);
    }

    /**
     * 特殊消息无需发送给用户
     * @param chatId
     * @param handler
     */
    public static void setNotNeedSend(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String msgId = update.getMessage().getMessageId().toString();
        getCallbackDataService().set(BotRedisConstant.NOT_NEED_SEND + chatId + msgId, chatId);
    }

    /**
     * 特殊消息无需发送给用户
     * @param message
     */
    public static void setNotNeedSend(Message message) {
        setNotNeedSend(message.getChatId(), message.getMessageId());
    }

    /**
     * 特殊消息无需发送给用户
     * @param chatId
     * @param messageId
     */
    public static void setNotNeedSend(Long chatId, Integer messageId) {
        String chatIdStr = chatId.toString();
        getCallbackDataService().set(BotRedisConstant.NOT_NEED_SEND + chatIdStr + messageId, chatIdStr);
    }

    /**
     * 判断当前消息是否为特殊消息，无需转发
     * @param chatId
     * @param handler
     */
    public static boolean getNotNeedSend(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String msgId = update.getMessage().getMessageId().toString();
        String ret = getCallbackDataService().get(BotRedisConstant.NOT_NEED_SEND + chatId + msgId);
        return ret != null;
    }

    /**
     * 发送文本、图片、按钮混合消息
     * @param botAgent
     * @param chatId
     * @param mediaType
     * @param txt
     * @param btnList
     * @param mediaFile
     */
    public static Message sendTextMsg(BaseBot botAgent, Long chatId, int mediaType, String txt, String btnList, String mediaFile, Integer replyMsgId, ReplyKeyboardMarkup replyKeyboardMarkup) {
        if((StringUtils.isBlank(mediaFile) && StringUtils.isBlank(txt))) {
            return null;
        }

        if(StringUtils.isNotBlank(mediaFile)) {
            //媒体文件+按钮
            java.io.File file = new java.io.File(uploadpath + mediaFile);
            InputFile inputFile = new InputFile(file);
            Message sendMsg = null;
            try {
                if(mediaType == 3) {
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(chatId);
                    sendDocument.setDocument(inputFile);
                    sendDocument.setCaption(txt);
                    if(replyKeyboardMarkup == null) {
                        sendDocument.setReplyMarkup(botAgent.createMarkup(btnList));
                    } else {
                        sendDocument.setReplyMarkup(replyKeyboardMarkup);
                    }
                    sendDocument.setReplyToMessageId(replyMsgId);
                    sendDocument.setParseMode("html");
                    sendMsg = botAgent.execute(sendDocument);
                }

                if(mediaType == 1) {
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(chatId);
                    sendPhoto.setPhoto(inputFile);
                    sendPhoto.setCaption(txt);
                    if(replyKeyboardMarkup == null) {
                        sendPhoto.setReplyMarkup(botAgent.createMarkup(btnList));
                    } else {
                        sendPhoto.setReplyMarkup(replyKeyboardMarkup);
                    }
                    sendPhoto.setReplyToMessageId(replyMsgId);
                    sendPhoto.setParseMode("html");
                    sendMsg = botAgent.execute(sendPhoto);
                }

                if(mediaType == 2) {
                    SendVideo sendVideo = new SendVideo();
                    sendVideo.setChatId(chatId);
                    sendVideo.setVideo(inputFile);
                    sendVideo.setCaption(txt);
                    if(replyKeyboardMarkup == null) {
                        sendVideo.setReplyMarkup(botAgent.createMarkup(btnList));
                    } else {
                        sendVideo.setReplyMarkup(replyKeyboardMarkup);
                    }
                    sendVideo.setReplyToMessageId(replyMsgId);
                    sendVideo.setParseMode("html");
                    sendMsg = botAgent.execute(sendVideo);
                }
                if(sendMsg != null) {
                    BotUtil.setNotNeedSend(sendMsg);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return sendMsg;
        }
        else {
            //文本+按钮
            Message message = null;
            if(replyKeyboardMarkup == null) {
                message = botAgent.sendMsgWithBtn(chatId, txt, botAgent.createMarkup(btnList), replyMsgId);
            } else {
                message =botAgent.sendMsgWithBtn(chatId, txt, replyKeyboardMarkup, replyMsgId);
            }
            return message;
        }
    }

    /**
     * 发送文本、图片、按钮混合消息
     * @param botAgent
     * @param chatId
     * @param mediaType
     * @param txt
     * @param btnList
     * @param mediaFile
     */
    public static Message sendTextMsg(BaseBot botAgent, Long chatId, int mediaType, String txt, String btnList, String mediaFile) {
        return sendTextMsg(botAgent, chatId, mediaType, txt, btnList, mediaFile, null, null);
    }

    /**
     * 发送媒体文件消息
     * @param botAgent
     * @param chatId
     * @param mediaType
     * @param mediaFile
     */
    public static void sendMediaMsg(BaseBot botAgent, Long chatId, int mediaType, String mediaFile) {
        java.io.File file = new java.io.File(uploadpath + mediaFile);
        InputFile inputFile = new InputFile(file);

        // 发送文件
        try {
            if(mediaType == 3) {
                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(chatId);
                sendDocument.setDocument(inputFile);
                botAgent.execute(sendDocument);
            }

            if(mediaType == 1) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(inputFile);
                botAgent.execute(sendPhoto);
            }

            if(mediaType == 2) {
                SendVideo sendVideo = new SendVideo();
                sendVideo.setChatId(chatId);
                sendVideo.setVideo(inputFile);
                botAgent.execute(sendVideo);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static ITgCallbackDataService getCallbackDataService() {
        if(callbackDataService == null) {
            callbackDataService = getBean(ITgCallbackDataService.class);
        }
        return callbackDataService;
    }

    /**
     * 根据IP获取所属地
     * @param ipAddress
     * @return
     */
    public static String getLocationByIP(String ipAddress) {
        String location = "";
        try {
            String apiUrl = "http://ip-api.com/json/" + ipAddress + "?lang=zh-CN";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            location = response.toString();
            JSONObject json = JSON.parseObject(location);
            if(!"success".equals(json.getString("status"))) {
                return "";
            }
            location = json.getString("country") + " / " + json.getString("regionName") + " / " + json.getString("city");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return location;
    }

    /**
     * 广告尾巴
     * @return
     */
    public static String adTailText() {
        return "<b>由 " + "<a href=\""+techSupportUrl+"\">"+techSupportUsername+"</a>" + " 提供技术支持</b>";
    }

    /**
     * 判断当前版本是否要加广告尾巴
     * @param bot
     * @param msg
     * @return
     */
    public static String checkAdTail(TgBot bot, String msg) {
        if(bot.getServiceType() == 1) {
            msg = msg + "\n\n" + BotUtil.adTailText();
        }
        return msg;
    }

    /**
     * 升级专业版提示语
     * @param botAgent
     * @param chatId
     * @param tips
     * @return
     */
    public static void upgradeTips(BaseBot botAgent, Long chatId, String tips) {
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        btnBuilder.addRow().addDataButton("升级专业版", UpgradeBotHandler.class);
        botAgent.sendMsgWithData(chatId, tips, btnBuilder.build());
    }

    /**
     * 获取用户全名
     * @param user
     * @return
     */
    public static String getFullname(User user) {
        String fullname = (user.getFirstName()!=null ? user.getFirstName() : "") + (user.getLastName()!=null ? user.getLastName() : "");
        return fullname;
    }

    /**
     * 设置底部按钮
     * @param txt
     */
    public static Set<KeyboardRow> parseBottomBtn(String txt) {
        Set<KeyboardRow> keyboardRowSet = new HashSet<>();
        String[] rowStrList = StringUtils.split(txt, "\n");
        for(String row : rowStrList) {
            KeyboardRow keyboardRow = new KeyboardRow();
            String[] cmdList = row.split("\\|");
            for (String cmd : cmdList) {
                String cmdTxt = cmd.trim();
                KeyboardButton btn = KeyboardButton.builder().text(cmdTxt).build();
                keyboardRow.add(btn);
            }
            keyboardRowSet.add(keyboardRow);
        }
        return keyboardRowSet;
    }

}
