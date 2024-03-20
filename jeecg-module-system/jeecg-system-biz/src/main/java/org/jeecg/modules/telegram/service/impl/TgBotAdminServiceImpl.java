package org.jeecg.modules.telegram.service.impl;

import org.jeecg.modules.telegram.entity.TgBotAdmin;
import org.jeecg.modules.telegram.mapper.TgBotAdminMapper;
import org.jeecg.modules.telegram.service.ITgBotAdminService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 机器人管理员表
 * @Author: jeecg-boot
 * @Date:   2023-11-30
 * @Version: V1.0
 */
@Service
public class TgBotAdminServiceImpl extends ServiceImpl<TgBotAdminMapper, TgBotAdmin> implements ITgBotAdminService {

}
