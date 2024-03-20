package org.jeecg.modules.telegram.service;

import org.jeecg.modules.telegram.entity.TgBot;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 机器人表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
public interface ITgBotService extends IService<TgBot> {

    TgBot getByTgBotId(String botId);

    TgBot getByBotName(String botName);

    TgBot getByToken(String token);

    /**
     * 双向机器的start命令的消息按钮
     * @param userId
     * @return
     */
    ArrayList<List<InlineButtonVO>> twowayBotButton(String userId);
}
