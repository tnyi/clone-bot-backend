package org.jeecg.modules.telegram.service;

import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.entity.TgChat;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 聊天信息表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
public interface ITgChatService extends IService<TgChat> {

    /**
     * 将用户账号同步到web im
     * @param botAgent
     * @param newChat
     */
    void registerUserToWebIM(BaseBot botAgent, TgChat newChat);

}
