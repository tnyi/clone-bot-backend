package org.jeecg.modules.telegram.handler.agent.web;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgWebDevice;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgWebDeviceService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.websocket.IMWebSocket;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理代理机器人的"进入网页版-管理登录设备-设备下线"指令
 */
@Slf4j
@Service
public class WebDeviceOfflineHandler extends BaseBotDataHandler {

    @Resource
    private ITgWebDeviceService tgWebDeviceService;

    @Resource
    private IMWebSocket imWebSocket;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = getCallbackData(update);
        //通知IM前端退出登录
        TgWebDevice device = tgWebDeviceService.getById(data);
        JSONObject wsMsg = new JSONObject();
        wsMsg.put("cmd", 2);
        imWebSocket.pushMessage(device.getDeviceNo(), wsMsg.toJSONString());

        device.setStatus(2);
        tgWebDeviceService.updateById(device);

        List<TgWebDevice> deviceList = tgWebDeviceService.lambdaQuery()
                                        .eq(TgWebDevice::getBotId, botAgent.getDbBotId())
                                        .eq(TgWebDevice::getStatus, 1)
                                        .orderByDesc(TgWebDevice::getCreateTime)
                                        .last("limit 3").list();
        int curCount = deviceList.size();
        String msg = "\uD83C\uDF10 当前登录设备个数：" + (curCount >0 ? curCount : 0);
        botAgent.sendEditMsg(message.getChatId(), msg, buildBtnList(deviceList), message.getMessageId());
    }

    private ArrayList<List<InlineButtonVO>> buildBtnList(List<TgWebDevice> deviceList) {
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        for(TgWebDevice device : deviceList) {
            btnBuilder = btnBuilder
                    .addRow()
                    .addDataButton("\uD83D\uDCBB" + device.getUsername(), device.getId(), WebDeviceInfoHandler.class)
                    .addDataButton("\uD83D\uDCF5强制退出", device.getId(), WebDeviceMgrHandler.class);
        }
        btnBuilder = btnBuilder
                    .addRow()
                    .addDataButton("返回", WebVerHandler.class);
        return btnBuilder.build();
    }

}
