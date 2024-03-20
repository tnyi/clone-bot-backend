package org.jeecg.modules.telegram.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.IpUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.telegram.bot.BotAgent;
import org.jeecg.modules.telegram.bot.BotAgentManager;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.entity.TgWebDevice;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.handler.agent.web.AuthWebLoginHandler;
import org.jeecg.modules.telegram.handler.agent.web.CancelWebLoginHandler;
import org.jeecg.modules.telegram.handler.agent.web.WebVerHandler;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.service.ITgWebDeviceService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 网页版相关接口
 * @Author: jeecg-boot
 * @Date: 2023-11-15
 * @Version: V1.0
 */
@Api(tags = "网页版")
@RestController
@RequestMapping("/bot/web")
@Slf4j
public class TgWebVersionController extends JeecgController<TgBot, ITgBotService> {

    @Resource
    private ITgBotService tgBotService;

    @Resource
    private ITgChatService tgChatService;

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgWebDeviceService tgWebDeviceService;

    /**
     * 登录请求
     *
     * @param loginReqVO
     * @return
     */
    @AutoLog(value = "网页版-登录请求")
    @ApiOperation(value = "网页版-登录请求", notes = "网页版-登录请求")
    @PostMapping(value = "/login")
    public Result<String> login(@RequestBody WebLoginReqVO loginReqVO, HttpServletRequest req) {
        TgBot tgBot = tgBotService.lambdaQuery().eq(TgBot::getWebLoginToken, loginReqVO.getToken()).one();
        if(tgBot == null) {
            return Result.error("登录链接非法");
        }
        BotAgent botAgent = BotAgentManager.queryById(tgBot.getId());
        String userAgent = req.getHeader("User-Agent");
        String ipAddr = IpUtils.getIpAddr(req);
        String location = BotUtil.getLocationByIP(ipAddr);
        String msg = "\uD83C\uDF10 收到来一条网页登录请求，请确认以下登录信息:\n" +
                "\n" +
                "- 用户名：" + loginReqVO.getUsername() + "\n" +
                "- 登录ip：" + ipAddr + "\n" +
                "- 归属地：" + location + "\n" +
                "- 设备：电脑\n" +
                "- 浏览器：" + userAgent + "\n" +
                "\n" +
                "\uD83D\uDC49 确认无误点击授权登录！\n" +
                "\n" +
                "如果不是您的登录请求，请立即重置网页登录链接，以防泄露。";
        String deviceNo = UUIDGenerator.generate();
        TgWebDevice webDevice = new TgWebDevice();
        webDevice.setDeviceNo(deviceNo);
        webDevice.setBrowser(userAgent);
        webDevice.setDeviceType(1);
        webDevice.setBotId(tgBot.getId());
        webDevice.setLocation(location);
        webDevice.setLoginIp(ipAddr);
        webDevice.setUsername(loginReqVO.getUsername());
        webDevice.setStatus(2);
        callbackDataService.set(BotRedisConstant.WEB_DEVICE_INFO + deviceNo, JSON.toJSONString(webDevice));

        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        ArrayList<List<InlineButtonVO>> butList = btnBuilder
                                                        .addRow()
                                                        .addDataButton("✅授权登录", deviceNo, AuthWebLoginHandler.class)
                                                        .addDataButton("\uD83D\uDEAB取消", CancelWebLoginHandler.class)
                                                        .build();
        TgChat tgChat = tgChatService.lambdaQuery().eq(TgChat::getBotId, botAgent.getDbBotId()).eq(TgChat::getChatType, 1).last("limit 1").one();
        if(tgChat == null) {
            return Result.error("当前还未配置管理员会话");
        }

        botAgent.sendMsgWithData(Long.parseLong(tgChat.getChatId()), msg, butList);
        return Result.OK(deviceNo);
    }

    /**
     * 发送消息给TG机器人聊天群
     *
     * @param sendMsgToTGVO
     * @return
     */
    @AutoLog(value = "网页版-发送消息到TG")
    @ApiOperation(value = "网页版-发送消息到TG", notes = "网页版-发送消息到TG")
    @PostMapping(value = "/sendMsgToTG")
    public Result<String> sendMsgToTG(@RequestBody SendMsgToTGVO sendMsgToTGVO) {
        TgChat tgChat = tgChatService.lambdaQuery().eq(TgChat::getBotId, sendMsgToTGVO.getBotId()).eq(TgChat::getChatId, sendMsgToTGVO.getChatId()).last("limit 1").one();
        if(tgChat == null) {
            return Result.error("telegram聊天信息不存在");
        }
        TgBot tgBot = tgBotService.getById(tgChat.getBotId());
        if(tgBot == null) {
            return Result.error("机器人不存在");
        }
        BotAgent botAgent = BotAgentManager.queryById(tgBot.getId());
        botAgent.sendMsg(Long.parseLong(sendMsgToTGVO.getChatId()), sendMsgToTGVO.getContent());
        return Result.ok("发送成功");
    }

    /**
     * 网页版退出
     *
     * @param logoutReqVO
     * @return
     */
    @AutoLog(value = "网页版-退出")
    @ApiOperation(value = "网页版-退出", notes = "网页版-退出")
    @PostMapping(value = "/logout")
    public Result<String> logout(@RequestBody WebLogoutReqVO logoutReqVO) {
        TgWebDevice device = tgWebDeviceService.lambdaQuery().eq(TgWebDevice::getDeviceNo, logoutReqVO.getUsername()).one();
        if(device != null) {
            device.setStatus(2);
            tgWebDeviceService.updateById(device);
        }
        return Result.ok();
    }

    /**
     * 查询历史网页版客户端信息
     *
     * @param reqVO
     * @return
     */
    @AutoLog(value = "网页版-查询历史网页版客户端信息")
    @ApiOperation(value = "网页版-查询历史网页版客户端信息", notes = "网页版-查询历史网页版客户端信息")
    @PostMapping(value = "/queryHistDeviceInfo")
    public Result<QueryHisDeviceInfoResVO> queryHistDeviceInfo(@RequestBody QueryHisDeviceInfoReqVO reqVO) {
        TgWebDevice device = tgWebDeviceService.lambdaQuery().eq(TgWebDevice::getDeviceNo, reqVO.getUsername()).one();
        if(device != null) {
            TgBot bot = tgBotService.getById(device.getBotId());
            List<TgWebDevice> deviceList = tgWebDeviceService.lambdaQuery().eq(TgWebDevice::getBotId, device.getBotId()).list();
            if(!CollectionUtils.isEmpty(deviceList)) {
                List<String> deviceNoList = deviceList.stream().map(e->e.getDeviceNo()).collect(Collectors.toList());
                QueryHisDeviceInfoResVO resVO = new QueryHisDeviceInfoResVO();
                resVO.setDeviceNoList(deviceNoList);
                resVO.setSaveTime(bot.getWebMsgSaveTime());
                return Result.ok(resVO);
            }
        }
        return Result.ok();
    }
}
