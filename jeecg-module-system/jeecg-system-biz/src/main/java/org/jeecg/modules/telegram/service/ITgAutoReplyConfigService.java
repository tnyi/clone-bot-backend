package org.jeecg.modules.telegram.service;

import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.telegram.vo.MsgContentVO;

import java.util.List;

/**
 * @Description: 自动回复配置
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
public interface ITgAutoReplyConfigService extends IService<TgAutoReplyConfig> {

    /**
     * 根据名称获取自动回复配置项
     * @param botId
     * @param configName
     * @return
     */
    TgAutoReplyConfig getByConfigName(String botId, String configName);

    /**
     * 生成自动回复主界面数据结构
     * @param botId
     * @return
     */
    MsgContentVO autoReplyConfigMsg(String botId);

    /**
     * 生成自动回复主界面数据结构
     * @param configList
     * @return
     */
    MsgContentVO autoReplyConfigMsg(List<TgAutoReplyConfig> configList);

    /**
     * 生成自动回复配置具体条目信息界面的数据结构
     * @param config
     * @return
     */
    MsgContentVO configItemMsg(TgAutoReplyConfig config);
}
