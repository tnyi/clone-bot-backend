package org.jeecg.modules.telegram.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

/**
 * @Description: 黑名单表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
public interface ITgBlacklistService extends IService<TgBlacklist> {

    TgBlacklist getByUserId(String botId, String tgUserId);

    /**
     * 黑名单功能菜单
     * @param botId
     * @param pageNo
     * @param pageSize
     * @return
     */
    MsgContentVO blacklistConfigMsg(String botId, int pageNo, int pageSize);

    /**
     * 黑名单功能菜单
     * @param bot
     * @param pageList
     * @return
     */
    MsgContentVO blacklistConfigMsg(BaseBot bot, IPage<TgBlacklist> pageList);

    /**
     * /ban指令拉黑用户菜单
     * @param bot
     * @param message
     * @param tgBot
     * @return
     */
    MsgContentVO banUserMsg(BaseBot bot, Message message, TgBot tgBot);

    /**
     * /ban指令拉黑用户菜单
     * @param botAgent
     * @param user
     * @param chatId
     * @param tgBot
     * @return
     */
    MsgContentVO banUserMsg(BaseBot botAgent, User user, Long chatId, TgBot tgBot);
}
