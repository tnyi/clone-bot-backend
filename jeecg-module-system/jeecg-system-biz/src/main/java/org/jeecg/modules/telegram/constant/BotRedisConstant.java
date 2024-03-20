package org.jeecg.modules.telegram.constant;

public class BotRedisConstant {

    /**保存需要特殊处理的消息聊天ID*/
    public static final String BIZ_REPLY_CHAT_ID = "tg:bot:bizReply:chatId:";

    /**保存需要处理的详细条目聊天ID*/
    public static final String EDIT_DATA_ITEM_CHAT_ID = "tg:bot:editDataItem:chatId:";

    /**按钮点击回调的处理类*/
    public static final String CALLBACK_CHAT_ID = "tg:bot:callback:handler:";

    /**网页版设备信息*/
    public static final String WEB_DEVICE_INFO = "tg:bot:web:device:";

    /**广播消息统计前缀*/
    public static final String BROADCAST_STAT = "tg:bot:broadcast:stat:";

    /**管理员发送消息前缀*/
    public static final String ADMIN_SEND_MSG = "tg:bot:admin:sendMsg:";

    /**特殊无需转发的消息前缀*/
    public static final String NOT_NEED_SEND = "tg:bot:admin:notneedsend:";

    /**群组状态修改类型*/
    public static final String GROUP_CHAT_EDIT_TYPE = "tg:bot:groupchat:edittype:";

    /**通用超时时间*/
    public static int COMMON_TIMEOUT = 3600;

}
