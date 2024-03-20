package org.jeecg.modules.telegram.handler.agent.web;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgWebDevice;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.service.ITgWebDeviceService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理代理机器人的"进入网页版-管理登录设备-查看设备信息"指令
 */
@Slf4j
@Service
public class WebDeviceInfoHandler extends BaseBotDataHandler {

    @Resource
    private ITgWebDeviceService tgWebDeviceService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = getCallbackData(update);
        TgWebDevice device = tgWebDeviceService.getById(data);
        String msg =
                "- 用户名：" + device.getUsername() + "\n" +
                "- 登录ip：" + device.getLoginIp() + "\n" +
                "- 归属地：" + device.getLocation() + "\n" +
                "- 设备：电脑\n" +
                "- 浏览器：" + device.getBrowser() + "\n";
        botAgent.sendMsg(message.getChatId(), msg);
    }

}
