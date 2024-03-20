package org.jeecg.modules.telegram.websocket;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.base.BaseMap;
import org.jeecg.common.constant.WebsocketConst;
import org.jeecg.common.modules.redis.client.JeecgRedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于网页版登录通信
 */
@Component
@Slf4j
@ServerEndpoint("/bot/im/{deviceNo}")
public class IMWebSocket {

    /**线程安全Map*/
    private static ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();


    //==========【websocket接受、推送消息等方法 —— 具体服务节点推送ws消息】========================================================================================
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "deviceNo") String deviceNo) {
        try {
            sessionPool.put(deviceNo, session);
            log.debug("【系统 WebSocket】有新的连接，总数为:" + sessionPool.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @OnClose
    public void onClose(@PathParam("deviceNo") String deviceNo) {
        try {
            sessionPool.remove(deviceNo);
            log.debug("【系统 WebSocket】连接断开，总数为:" + sessionPool.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * ws推送消息
     *
     * @param deviceNo
     * @param message
     */
    public void pushMessage(String deviceNo, String message) {
        for (Map.Entry<String, Session> item : sessionPool.entrySet()) {
            //userId key值= {用户id + "_"+ 登录token的md5串}
            //TODO vue2未改key新规则，暂时不影响逻辑
            if (item.getKey().contains(deviceNo)) {
                Session session = item.getValue();
                try {
                    //update-begin-author:taoyan date:20211012 for: websocket报错 https://gitee.com/jeecg/jeecg-boot/issues/I4C0MU
                    synchronized (session){
                        log.debug("【系统 WebSocket】推送单人消息:" + message);
                        session.getBasicRemote().sendText(message);
                    }
                    //update-end-author:taoyan date:20211012 for: websocket报错 https://gitee.com/jeecg/jeecg-boot/issues/I4C0MU
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
    }

    /**
     * ws遍历群发消息
     */
    public void pushMessage(String message) {
        try {
            for (Map.Entry<String, Session> item : sessionPool.entrySet()) {
                try {
                    item.getValue().getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.debug("【系统 WebSocket】群发消息:" + message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * ws接受客户端消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam(value = "deviceNo") String deviceNo) {
        if(!"ping".equals(message) && !WebsocketConst.CMD_CHECK.equals(message)){
            log.debug("【系统 WebSocket】收到客户端消息:" + message);
        }else{
            log.debug("【系统 WebSocket】收到客户端消息:" + message);
        }

//        //------------------------------------------------------------------------------
//        JSONObject obj = new JSONObject();
//        //业务类型
//        obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_CHECK);
//        //消息内容
//        obj.put(WebsocketConst.MSG_TXT, "心跳响应");
//        this.pushMessage(userId, obj.toJSONString());
//        //------------------------------------------------------------------------------
    }

    /**
     * 配置错误信息处理
     *
     * @param session
     * @param t
     */
    @OnError
    public void onError(Session session, Throwable t) {
        log.warn("【系统 WebSocket】消息出现错误");
        log.error(t.getMessage(), t);
    }
    //==========【系统 WebSocket接受、推送消息等方法 —— 具体服务节点推送ws消息】========================================================================================

}
