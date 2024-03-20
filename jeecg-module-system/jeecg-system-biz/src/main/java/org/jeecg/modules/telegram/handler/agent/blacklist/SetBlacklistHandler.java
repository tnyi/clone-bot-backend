package org.jeecg.modules.telegram.handler.agent.blacklist;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import org.jeecg.modules.telegram.handler.BaseBotDataHandler;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.service.ITgBlacklistService;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

/**
 * 处理代理机器人的"黑名单"指令
 */
@Slf4j
@Service
public class SetBlacklistHandler extends BaseBotDataHandler {

    @Resource
    private ITgBlacklistService blacklistService;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getCallbackQuery().getMessage();
        String data = getCallbackData(update);
        int pageNo = 1;
        if(data != null) {
            try {
                pageNo = Integer.parseInt(data);
            } catch (Exception e) {
                pageNo = 1;
            }
        }
        Page<TgBlacklist> page = new Page<TgBlacklist>(pageNo, 10);
        Wrapper pageWrapper = Wrappers.<TgBlacklist>lambdaQuery().eq(TgBlacklist::getBotId, botAgent.getDbBotId()).orderByDesc(TgBlacklist::getCreateTime);
        IPage<TgBlacklist> pageList = blacklistService.page(page, pageWrapper);

        String msg = "❌ 黑名单\n" +
                        "\n" +
                        "\uD83D\uDC49 回复用户 /ban 或者 /unban 可以拉黑和解禁用户\n" +
                        "\n" +
                (CollectionUtils.isEmpty(pageList.getRecords()) ?  "未找到任何黑名单数据" : "共" + pageList.getTotal() + "条黑名单记录");

        MsgContentVO contentVO = blacklistService.blacklistConfigMsg(botAgent, page);
        botAgent.sendEditMsg(message.getChatId(), msg, contentVO.getButtonList(), message.getMessageId());
    }

}
