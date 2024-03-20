package org.jeecg.modules.telegram.service.impl;

import com.alibaba.fastjson.JSON;
import org.jeecg.modules.telegram.entity.TgCallbackData;
import org.jeecg.modules.telegram.mapper.TgCallbackDataMapper;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.vo.CallbackQueryData;
import org.jeecg.modules.telegram.vo.TxtMsgHandlerVO;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 消息回调数据表
 * @Author: jeecg-boot
 * @Date:   2023-11-22
 * @Version: V1.0
 */
@Service
public class TgCallbackDataServiceImpl extends ServiceImpl<TgCallbackDataMapper, TgCallbackData> implements ITgCallbackDataService {

    @Override
    public void set(String key, String value) {
        TgCallbackData data = this.lambdaQuery().eq(TgCallbackData::getDataKey, key).one();
        if(data == null) {
            data = new TgCallbackData();
            data.setDataKey(key);
            data.setDataValue(value);
            this.save(data);
        } else {
            data.setDataValue(value);
            this.updateById(data);
        }
    }

    @Override
    public void set(String key, CallbackQueryData value) {
        String jsonValue = JSON.toJSONString(value);
        this.set(key, jsonValue);
    }

    @Override
    public void set(String key, TxtMsgHandlerVO value) {
        String jsonValue = JSON.toJSONString(value);
        this.set(key, jsonValue);
    }

    @Override
    public String get(String key) {
        TgCallbackData data = this.lambdaQuery().eq(TgCallbackData::getDataKey, key).one();
        return data != null ? data.getDataValue() : null;
    }

    @Override
    public CallbackQueryData getCallbackData(String key) {
        String data = get(key);
        return JSON.parseObject(data, CallbackQueryData.class);
    }

    @Override
    public TxtMsgHandlerVO getTxtMsgHandler(String key) {
        String data = get(key);
        return JSON.parseObject(data, TxtMsgHandlerVO.class);
    }

    @Override
    public void del(String key) {
        this.lambdaUpdate().eq(TgCallbackData::getDataKey, key).remove();
    }
}
