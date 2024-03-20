package org.jeecg.modules.telegram.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息内容结构
 */

@Data
public class MsgContentVO {

    /**消息文本*/
    private String text;

    /**按钮列表*/
    private ArrayList<List<InlineButtonVO>> buttonList;

}
