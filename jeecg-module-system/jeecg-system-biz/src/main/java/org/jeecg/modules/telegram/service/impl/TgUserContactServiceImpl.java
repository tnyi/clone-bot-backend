package org.jeecg.modules.telegram.service.impl;

import org.jeecg.modules.telegram.entity.TgUserContact;
import org.jeecg.modules.telegram.mapper.TgUserContactMapper;
import org.jeecg.modules.telegram.service.ITgUserContactService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户的联系人
 * @Author: jeecg-boot
 * @Date:   2023-12-06
 * @Version: V1.0
 */
@Service
public class TgUserContactServiceImpl extends ServiceImpl<TgUserContactMapper, TgUserContact> implements ITgUserContactService {

}
