package org.jeecg.modules.telegram.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.bot.BotAgentManager;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.agent.CancelOperationHandler;
import org.jeecg.modules.telegram.handler.agent.ViewEmptyBtnHandler;
import org.jeecg.modules.telegram.handler.agent.blacklist.AddBlacklistHandler;
import org.jeecg.modules.telegram.handler.agent.blacklist.SetBlacklistHandler;
import org.jeecg.modules.telegram.handler.agent.blacklist.UnlockUserHandler;
import org.jeecg.modules.telegram.handler.agent.sendmsg.BanUserHandler;
import org.jeecg.modules.telegram.handler.agent.sendmsg.SwitchRecvUserHandler;
import org.jeecg.modules.telegram.handler.agent.sendmsg.ViewUserInfoHandler;
import org.jeecg.modules.telegram.mapper.TgBlacklistMapper;
import org.jeecg.modules.telegram.service.ITgBlacklistService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.DelBlacklistVO;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description: 黑名单表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Service
public class TgBlacklistServiceImpl extends ServiceImpl<TgBlacklistMapper, TgBlacklist> implements ITgBlacklistService {

    @Override
    public TgBlacklist getByUserId(String botId, String tgUserId) {
        return this.lambdaQuery().eq(TgBlacklist::getBotId, botId).eq(TgBlacklist::getTgUserId, tgUserId).one();
    }

    @Override
    public MsgContentVO blacklistConfigMsg(String botId, int pageNo, int pageSize) {
        Page<TgBlacklist> page = new Page<TgBlacklist>(pageNo, pageSize);
        Wrapper pageWrapper = Wrappers.<TgBlacklist>lambdaQuery().eq(TgBlacklist::getBotId, botId).orderByDesc(TgBlacklist::getCreateTime);
        IPage<TgBlacklist> pageList = this.page(page, pageWrapper);
        BaseBot bot = BotAgentManager.queryById(botId);
        return blacklistConfigMsg(bot, pageList);
    }

    @Override
    public MsgContentVO blacklistConfigMsg(BaseBot bot, IPage<TgBlacklist> dataPage) {
        List<TgBlacklist> configList = dataPage.getRecords();
        if(CollectionUtils.isEmpty(configList)) {
            configList = new ArrayList<>();
        }
        int pageNo = (int)dataPage.getCurrent();
        int preNo = pageNo - 1;
        int nextNo = pageNo + 1;
        if(preNo <= 0) {
            preNo = 1;
        }
        if(nextNo > dataPage.getPages()) {
            nextNo = pageNo;
        }
        if(CollectionUtils.isEmpty(configList)) {
            pageNo = 0;
        }

        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        for(TgBlacklist config : configList) {
            DelBlacklistVO delVO = new DelBlacklistVO();
            delVO.setPageNo(pageNo);
            delVO.setPageSize((int)dataPage.getSize());
            delVO.setUserId(config.getTgUserId());

            HashMap dataMap = new HashMap();
            dataMap.put("userId", config.getTgUserId());
            dataMap.put("username", config.getTgUsername());
            dataMap.put("chatId", config.getChatId());
            String dataMapStr = JSON.toJSONString(dataMap);

            GetChatMember getChatMember = GetChatMember.builder().chatId(config.getChatId()).userId(Long.parseLong(config.getTgUserId())).build();
            ChatMember chatMember = bot.execute(getChatMember);
            btnBuilder = btnBuilder.addRow()
                    .addDataButton("\uD83D\uDCAC" + config.getTgUserId(), dataMapStr, ViewUserInfoHandler.class)
                    .addUrlButton(BotUtil.getFullname(chatMember.getUser()), "https://t.me/" + config.getTgUsername());
        }

        btnBuilder
                .addRow()
                .addDataButton("上一页", String.valueOf(preNo), SetBlacklistHandler.class)
                .addDataButton(pageNo + "/" + dataPage.getPages(), ViewEmptyBtnHandler.class)
                .addDataButton("下一页", String.valueOf(nextNo), SetBlacklistHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDEB7拉黑用户", AddBlacklistHandler.class)
                .addDataButton("\uD83D\uDFE2解封用户", UnlockUserHandler.class)
                .addRow()
                .addDataButton("返回", BackToMainHandler.class, true);
        MsgContentVO vo = new MsgContentVO();
        vo.setButtonList(btnBuilder.build());
        return vo;
    }

    @Override
    public MsgContentVO banUserMsg(BaseBot botAgent, Message message, TgBot tgBot) {
        return banUserMsg(botAgent, message.getFrom(), message.getChatId(), tgBot);
    }

    @Override
    public MsgContentVO banUserMsg(BaseBot botAgent, User user, Long chatId, TgBot tgBot) {
        String userId = String.valueOf(user.getId());
        String nickname = BotUtil.getFullname(user);
        HashMap dataMap = new HashMap();
        dataMap.put("userId", userId);
        dataMap.put("username", nickname);
        dataMap.put("chatId", chatId.toString());
        if(tgBot != null) {
            dataMap.put("srcType", "start");
        } else {
            dataMap.put("srcType", "ban");
        }
        String dataMapStr = JSON.toJSONString(dataMap);

        LinkedList<String> timeArr = new LinkedList<>();
        for(int i=1; i<=4; i++) {
            dataMap.put("banType", String.valueOf(i));
            String banMapStr = JSON.toJSONString(dataMap);
            timeArr.add(banMapStr);
        }

        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        if(tgBot != null) {
            btnBuilder.addRow().addDataButton("\uD83D\uDD04切换至：" + nickname, dataMapStr, SwitchRecvUserHandler.class);
        }
        btnBuilder.addRow()
                .addDataButton("\uD83D\uDEB71天", timeArr.get(0), BanUserHandler.class)
                .addDataButton("\uD83D\uDEB71周", timeArr.get(1), BanUserHandler.class)
                .addDataButton("\uD83D\uDEB71月", timeArr.get(2), BanUserHandler.class)
                .addDataButton("\uD83D\uDEB7永久", timeArr.get(3), BanUserHandler.class);
        if(tgBot != null) {
            btnBuilder.addRow()
                    .addDataButton("\uD83D\uDCAC" + userId, dataMapStr, ViewUserInfoHandler.class)
                    .addDataButton("\uD83E\uDD16Bot已自动回复" + tgBot.getReplyCount() + "次", ViewEmptyBtnHandler.class);
        } else {
            btnBuilder.addRow().addDataButton("\uD83D\uDEAB取消", CancelOperationHandler.class);
        }
        MsgContentVO vo = new MsgContentVO();
        vo.setButtonList(btnBuilder.build());
        return vo;
    }

}
