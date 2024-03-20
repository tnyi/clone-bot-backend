package org.jeecg.modules.telegram.service;

import org.jeecg.modules.telegram.entity.TgCallbackData;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.telegram.vo.CallbackQueryData;
import org.jeecg.modules.telegram.vo.TxtMsgHandlerVO;

/**
 * @Description: 消息回调数据表
 * @Author: jeecg-boot
 * @Date:   2023-11-22
 * @Version: V1.0
 */
public interface ITgCallbackDataService extends IService<TgCallbackData> {

    void set(String key, String value);

    void set(String key, CallbackQueryData value);

    void set(String key, TxtMsgHandlerVO value);

    String get(String key);

    CallbackQueryData getCallbackData(String key);

    TxtMsgHandlerVO getTxtMsgHandler(String key);

    void del(String key);
}
