package org.jeecg.modules.telegram.vo;

import lombok.Data;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.util.BotUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 内联按钮结构
 */
@Data
public class InlineButtonVO {

    private String text;

    private String url;

    private String data;

    public InlineButtonVO(String text, String url, String data) {
        super();
        this.text = text;
        this.url = url;
        this.data = data;
    }

    public static class InlineButtonBuilder {

        private ITgCallbackDataService callbackDataService = BotUtil.getBean(ITgCallbackDataService.class);

        private ArrayList<List<InlineButtonVO>> keyboard;

        private ArrayList<InlineButtonVO> row;

        public InlineButtonBuilder() {
            keyboard = new ArrayList<>();
        }

        public ArrayList<List<InlineButtonVO>> build() {
            return keyboard;
        }

        public InlineButtonBuilder addRow() {
            row = new ArrayList<>();
            keyboard.add(row);
            return this;
        }

        public InlineButtonBuilder addButton(String text, String url, String data) {
            InlineButtonVO vo = new InlineButtonVO(text, url, data);
            row.add(vo);
            checkRowSize();
            return this;
        }

        public InlineButtonBuilder addUrlButton(String text, String url) {
            InlineButtonVO vo = new InlineButtonVO(text, url, null);
            row.add(vo);
            checkRowSize();
            return this;
        }

        public InlineButtonBuilder addDataButton(String text, String data, Class handler) {
            return addDataButton(text, data, handler, false);
        }

        public InlineButtonBuilder addDataButton(String text, String data, Class handler, boolean delFlag) {
            String optId = UUIDGenerator.generate();
            callbackDataService.set(BotRedisConstant.CALLBACK_CHAT_ID + optId, new CallbackQueryData(data, handler, delFlag));
            InlineButtonVO vo = new InlineButtonVO(text, null, optId);
            row.add(vo);
            checkRowSize();
            return this;
        }

        public InlineButtonBuilder addDataButton(String text, Class handler) {
            return addDataButton(text, null, handler, false);
        }

        /**
         * 添加消息底部按钮
         * @param text
         * @param handler
         * @param delFlag  产生的缓存是否在处理后立即删除
         * @return
         */
        public InlineButtonBuilder addDataButton(String text, Class handler, boolean delFlag) {
            return addDataButton(text, null, handler, delFlag);
        }

        public void checkRowSize() {
            if(row.size() > 5) {
                throw new JeecgBootException("一行最多5个按钮");
            }
        }

    }

    public static InlineButtonBuilder builder() {
        return new InlineButtonVO.InlineButtonBuilder();
    }

}
