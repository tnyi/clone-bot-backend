package org.jeecg.modules.telegram.service.impl;

import org.jeecg.modules.telegram.entity.TgChatMsg;
import org.jeecg.modules.telegram.mapper.TgChatMsgMapper;
import org.jeecg.modules.telegram.service.ITgChatMsgService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户聊天信息表
 * @Author: jeecg-boot
 * @Date:   2023-12-06
 * @Version: V1.0
 */
@Service
public class TgChatMsgServiceImpl extends ServiceImpl<TgChatMsgMapper, TgChatMsg> implements ITgChatMsgService {

}
