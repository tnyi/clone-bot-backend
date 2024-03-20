package org.jeecg.modules.telegram.service;

import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.telegram.vo.MsgContentVO;

/**
 * @Description: 欢迎语配置
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
public interface ITgWelcomeConfigService extends IService<TgWelcomeConfig> {

    /**
     * 根据机器人id获取欢迎语配置
     * @param configId
     * @return
     */
    TgWelcomeConfig getBotWelcomeByConfigId(String configId);

    /**
     * 根据机器人id获取进群欢迎语配置
     * @param configId
     * @return
     */
    TgWelcomeConfig getGroupWelcomeById(String configId);

    /**
     * 根据配置ID生成配置机器人欢迎语的消息
     * @return
     */
    MsgContentVO welcomeConfigMsg(String configId);

    /**
     * 生成配置机器人欢迎语的消息
     * @return
     */
    MsgContentVO welcomeConfigMsg(TgWelcomeConfig config);

    /**
     * 根据配置ID生成配置进群欢迎语的消息
     * @return
     */
    MsgContentVO groupWelcomeConfigMsg(String configId);

    /**
     * 生成配置进群欢迎语的消息
     * @return
     */
    MsgContentVO groupWelcomeConfigMsg(TgWelcomeConfig config);

    /**
     * 生成欢迎语列表界面
     * @param botId
     * @return
     */
    MsgContentVO welcomeConfigListMsg(String botId);

    /**
     * 生成进群欢迎语列表界面
     * @param botId
     * @return
     */
    MsgContentVO groupWelcomeConfigListMsg(String botId);
}
