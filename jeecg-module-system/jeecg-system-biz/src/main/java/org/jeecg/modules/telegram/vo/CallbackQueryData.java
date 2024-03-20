package org.jeecg.modules.telegram.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户点击按钮，后台保存处理类以及携带的数据
 */
@Data
public class CallbackQueryData implements Serializable {

    /**携带的数据*/
    private String data;

    /**处理类*/
    private Class handler;

    /**处理完成后是否删除相关缓存数据*/
    private boolean delFlag = false;

    public CallbackQueryData() {

    }

    public CallbackQueryData(String data, Class handler) {
        this.data = data;
        this.handler = handler;
    }

    public CallbackQueryData(Class handler) {
        this.handler = handler;
    }

    public CallbackQueryData(String data, Class handler, boolean delFalg) {
        this.data = data;
        this.handler = handler;
        this.delFlag = delFalg;
    }

    public CallbackQueryData(Class handler, boolean delFlag) {
        this.handler = handler;
        this.delFlag = delFlag;
    }

}
