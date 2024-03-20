package org.jeecg.modules.telegram.vo;

import lombok.Data;

/**
 * 网页版发送消息给TG
 */
@Data
public class SendMsgToTGVO {

    //TG会话ID
    private String chatId;

    //消息内容
    private String content;

    //发送人用户名
    private String fromUsername;

    //TG机器人ID
    private String botId;

}
