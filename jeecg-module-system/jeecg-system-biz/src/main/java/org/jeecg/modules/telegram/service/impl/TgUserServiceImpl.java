package org.jeecg.modules.telegram.service.impl;

import org.jeecg.modules.telegram.entity.TgUser;
import org.jeecg.modules.telegram.mapper.TgUserMapper;
import org.jeecg.modules.telegram.service.ITgUserService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 机器人用户表
 * @Author: jeecg-boot
 * @Date:   2023-12-04
 * @Version: V1.0
 */
@Service
public class TgUserServiceImpl extends ServiceImpl<TgUserMapper, TgUser> implements ITgUserService {

}
