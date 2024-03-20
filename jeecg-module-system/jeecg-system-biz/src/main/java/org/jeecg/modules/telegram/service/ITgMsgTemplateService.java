package org.jeecg.modules.telegram.service;

import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.entity.TgMsgTemplate;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.telegram.vo.MsgContentVO;

import java.util.List;

/**
 * @Description: 消息模板表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
public interface ITgMsgTemplateService extends IService<TgMsgTemplate> {

    /**
     * 根据名称获取消息模板配置项
     * @param botId
     * @param templateName
     * @return
     */
    TgMsgTemplate getByConfigName(String botId, String templateName);

    MsgContentVO tepmlateConfigMsg(String botId);

    MsgContentVO tepmlateConfigMsg(List<TgMsgTemplate> configList);

    MsgContentVO configItemMsg(TgMsgTemplate config);
}
