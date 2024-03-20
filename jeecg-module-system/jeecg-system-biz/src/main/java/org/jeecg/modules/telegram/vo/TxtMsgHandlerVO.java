package org.jeecg.modules.telegram.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 普通文本消息处理类定义
 */
@Data
public class TxtMsgHandlerVO implements Serializable {

    /**处理类*/
    private Class handler;

    public TxtMsgHandlerVO() {

    }

    public TxtMsgHandlerVO(Class handler) {
        this.handler = handler;
    }

}
