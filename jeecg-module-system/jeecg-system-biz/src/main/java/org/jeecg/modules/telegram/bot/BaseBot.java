package org.jeecg.modules.telegram.bot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.SpringContextHolder;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.CallbackQueryData;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.vo.TxtMsgHandlerVO;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 平台的机器人基类
 */
@Slf4j
public abstract class BaseBot extends TelegramLongPollingBot {

    protected ITgCallbackDataService callbackDataService = SpringContextHolder.getApplicationContext().getBean(ITgCallbackDataService.class);

    /**缓存回复消息处理器实例*/
    protected LinkedHashMap<Class, BaseBotMsgHandler> replyHandlerCache = new LinkedHashMap<Class, BaseBotMsgHandler>();

    public abstract String getDbBotId();

    /**
     * 返回通用指令处理集合（key指令名称，value指令处理器)
     */
    public abstract LinkedHashMap<String, BaseBotMsgHandler> getCommandHandler();

    public BaseBot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    /**
     * 发送文本带底部按钮的消息
     * @param chatId
     * @param txtMsg
     * @param btnList
     */
    public Message sendMsgWithData(Long chatId, String txtMsg, List<List<InlineButtonVO>> btnList) {
        return sendMsgWithData(chatId, txtMsg, btnList, null);
    }

    /**
     * 发送文本带底部按钮的消息
     * @param chatId
     * @param txtMsg
     * @param btnList
     * @param replyMessageId
     */
    public Message sendMsgWithData(Long chatId, String txtMsg, List<List<InlineButtonVO>> btnList, Integer replyMessageId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = createMarkup(btnList);
        return sendMsgWithBtn(chatId, txtMsg, inlineKeyboardMarkup, replyMessageId);
    }

    /**
     * 发送文本带底部按钮的消息，按钮布局通过字符串的方式传入
     * @param chatId
     * @param txtMsg
     * @param buttonTextList  格式：
     *                        按钮1 - url1
     *                        按钮2 - url2 | 按钮3 - ulr3
     *                        按钮4 - url4
     */
    public Message sendMsgWithUrlBtn(Long chatId, String txtMsg, String buttonTextList) {
        return sendMsgWithUrlBtn(chatId, txtMsg, buttonTextList, null);
    }

    /**
     * 发送文本带底部按钮的消息，按钮布局通过字符串的方式传入
     * @param chatId
     * @param txtMsg
     * @param buttonTextList  格式：
     *                        按钮1 - url1
     *                        按钮2 - url2 | 按钮3 - ulr3
     *                        按钮4 - url4
     * @param replyMessageId
     */
    public Message sendMsgWithUrlBtn(Long chatId, String txtMsg, String buttonTextList, Integer replyMessageId) {
        String[] rowStrList = StringUtils.split(buttonTextList, "\n");
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for(int i=0; i<rowStrList.length; i++) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            String[] btnStrArr = rowStrList[i].split(" \\| ");
            for(int j=0; j<btnStrArr.length; j++) {
                String[] btnInfoArr = StringUtils.split(btnStrArr[j].trim(), " - ");
                InlineKeyboardButton btn = InlineKeyboardButton.builder().text(btnInfoArr[0].trim()).url(btnInfoArr[1].trim()).build();
                buttonList.add(btn);
            }
            rowList.add(buttonList);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboard(rowList).build();
        return sendMsgWithBtn(chatId, txtMsg, inlineKeyboardMarkup, replyMessageId);
    }

    /**
     * 发送文本带底部按钮的消息
     * @param chatId
     * @param txtMsg
     * @param inlineKeyboardMarkup
     */
    public Message sendMsgWithBtn(Long chatId, String txtMsg, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return sendMsgWithBtn(chatId, txtMsg, inlineKeyboardMarkup, null);
    }

    /**
     * 发送文本带底部按钮的消息
     * @param chatId
     * @param txtMsg
     * @param inlineKeyboardMarkup
     * @param replyMessageId
     */
    public Message sendMsgWithBtn(Long chatId, String txtMsg, ReplyKeyboard inlineKeyboardMarkup, Integer replyMessageId) {
        SendMessage replyMsg = SendMessage.builder()
                .text(txtMsg)
                .chatId(chatId)
                .parseMode("html")
                .replyMarkup(inlineKeyboardMarkup)
                .replyToMessageId(replyMessageId)
                .disableWebPagePreview(true)
                .build();
        return this.execute(replyMsg);
    }

    /**
     * 发送纯文本消息
     * @param chatId
     * @param txtMsg
     */
    public Message sendMsg(Long chatId, String txtMsg) {
        return sendReplyMsg(chatId, null, txtMsg);
    }

    /**
     * 发送纯文本消息
     * @param chatId
     * @param txtMsg
     * @param notNeedSend  是否允许发送给用户
     */
    public Message sendMsg(Long chatId, String txtMsg, boolean notNeedSend) {
        return sendReplyMsg(chatId, null, txtMsg, notNeedSend);
    }

    /**
     * 发送纯文本消息
     * @param chatId
     * @param msgId  需要回复的消息ID
     * @param txtMsg
     */
    public Message sendReplyMsg(Long chatId, Integer msgId, String txtMsg) {
        return sendReplyMsg(chatId, msgId, txtMsg, true);
    }

    /**
     * 发送纯文本消息
     * @param chatId
     * @param msgId  需要回复的消息ID
     * @param txtMsg
     * @param notNeedSend  是否允许发送给用户
     */
    public Message sendReplyMsg(Long chatId, Integer msgId, String txtMsg, boolean notNeedSend) {
        SendMessage replyMsg = SendMessage.builder()
                .text(txtMsg)
                .chatId(chatId)
                .replyToMessageId(msgId)
                .parseMode("html")
                .disableWebPagePreview(true)
                .build();
        Message msg = this.execute(replyMsg);
        if(notNeedSend) {
            BotUtil.setNotNeedSend(msg);
        }
        return msg;
    }

    /**
     * 修改已发送的消息
     * @param chatId
     * @param txtMsg
     * @param btnList
     * @param replyMessageId
     */
    public Integer sendEditMsg(Long chatId, String txtMsg, List<List<InlineButtonVO>> btnList, Integer replyMessageId) {

        InlineKeyboardMarkup inlineKeyboardMarkup = createMarkup(btnList);
        EditMessageText editMessageText = EditMessageText.builder()
                                            .text(txtMsg)
                                            .chatId(chatId)
                                            .parseMode("html")
                                            .replyMarkup(inlineKeyboardMarkup)
                                            .disableWebPagePreview(true)
                                            .messageId(replyMessageId).build();
        this.execute(editMessageText);
        return editMessageText.getMessageId();
    }

    /**
     * 修改已发送的消息
     * @param chatId
     * @param txtMsg
     * @param btnList
     * @param replyMessageId
     */
    public Integer sendEditCaption(Long chatId, String txtMsg, List<List<InlineButtonVO>> btnList, Integer replyMessageId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = createMarkup(btnList);
        EditMessageCaption editMessageText = EditMessageCaption.builder()
                .caption(txtMsg)
                .chatId(chatId)
                .parseMode("html")
                .replyMarkup(inlineKeyboardMarkup)
                .messageId(replyMessageId).build();
        this.execute(editMessageText);
        return editMessageText.getMessageId();
    }

    /**
     * 根据按钮数据生成tg按钮对象
     * @param btnList
     * @return
     */
    public InlineKeyboardMarkup createMarkup(List<List<InlineButtonVO>> btnList) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for(List<InlineButtonVO> voList : btnList) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            for(InlineButtonVO buttonVO : voList) {
                InlineKeyboardButton.InlineKeyboardButtonBuilder btnBuilder = InlineKeyboardButton.builder().text(buttonVO.getText());
                if(StringUtils.isNotBlank(buttonVO.getData())) {
                    btnBuilder = btnBuilder.callbackData(buttonVO.getData());
                }
                if(StringUtils.isNotBlank(buttonVO.getUrl())) {
                    btnBuilder = btnBuilder.url(buttonVO.getUrl());
                }
                InlineKeyboardButton btn = btnBuilder.build();
                buttonList.add(btn);
            }
            rowList.add(buttonList);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboard(rowList).build();
        return inlineKeyboardMarkup;
    }

    /**
     * 根据文本解析按钮
     * @param buttonTextList
     * @return
     */
    public InlineKeyboardMarkup createMarkup(String buttonTextList) {
        if(StringUtils.isBlank(buttonTextList)) {
            return null;
        }
        String[] rowStrList = StringUtils.split(buttonTextList, "\n");
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for(int i=0; i<rowStrList.length; i++) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            String[] btnStrArr = rowStrList[i].split(" \\| ");
            for(int j=0; j<btnStrArr.length; j++) {
                String[] btnInfoArr = StringUtils.split(btnStrArr[j].trim(), " - ");
                InlineKeyboardButton btn = InlineKeyboardButton.builder().text(btnInfoArr[0].trim()).url(btnInfoArr[1].trim()).build();
                buttonList.add(btn);
            }
            rowList.add(buttonList);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboard(rowList).build();
        return inlineKeyboardMarkup;
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method)  {
        try {
            return super.execute(method);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
            throw new JeecgBootException(e.getMessage(), e);
        }
    }

    /**
     * 通用消息处理逻辑
     * @param update
     */
    public void processUpdate(Update update) {
        BaseBot baseBot = this;
        //文本消息处理
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String text = message.getText();
            log.info("收到消息=" + text);

            //如果有设置业务处理逻辑则优先处理
            TxtMsgHandlerVO handlerVO = (TxtMsgHandlerVO) BotUtil.getTxtReplyHandler(baseBot.getDbBotId(), message.getChatId());
            if (handlerVO != null) {
                Class replyChatHandler = handlerVO.getHandler();
                BaseBotMsgHandler msgHandler = replyHandlerCache.get(replyChatHandler);
                if(msgHandler == null) {
                    msgHandler = (BaseBotMsgHandler)BotUtil.getBean(replyChatHandler);
                    replyHandlerCache.put(replyChatHandler, msgHandler);
                }
                msgHandler.process(baseBot, update);
                return;
            }

            if(StringUtils.isBlank(text)) {
                log.error("文本消息为空");
                return;
            }
            for (Map.Entry<String, BaseBotMsgHandler> entry : getCommandHandler().entrySet()) {
                if (Pattern.matches(entry.getKey(), text)) {
                    entry.getValue().process(baseBot, update);
                    return;
                }
            }
        }

        //按钮点击处理
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String btnData = callbackQuery.getData();
            Long chatId = callbackQuery.getMessage().getChatId();
            log.info("点击了内联按钮 : " + btnData);

            String cacheKey = BotRedisConstant.CALLBACK_CHAT_ID + btnData;
            CallbackQueryData callbackQueryData = callbackDataService.getCallbackData(cacheKey);
            if(callbackQueryData == null) {
                log.error("没有定义按钮数据 : " + btnData);
                return;
            }
            Class replyChatHandler = callbackQueryData.getHandler();
            if(replyChatHandler == null) {
                log.error("没有定义按钮处理逻辑类 : " + btnData);
                return;
            }

            BaseBotMsgHandler msgHandler = replyHandlerCache.get(replyChatHandler);
            if(msgHandler == null) {
                msgHandler = (BaseBotMsgHandler)BotUtil.getBean(replyChatHandler);
                replyHandlerCache.put(replyChatHandler, msgHandler);
            }
            if(StringUtils.isNotBlank(callbackQueryData.getData())) {
                callbackDataService.set(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId, callbackQueryData.getData());
            }
            msgHandler.process(baseBot, update);
            if(callbackQueryData.isDelFlag()) {
                callbackDataService.del(cacheKey);
            }
        }
    }
}
