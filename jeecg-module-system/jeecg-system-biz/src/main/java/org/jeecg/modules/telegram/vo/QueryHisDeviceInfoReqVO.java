package org.jeecg.modules.telegram.vo;

import lombok.Data;

/**
 * 根据当前网页版客户端信息查询历史网页版客户端信息
 */
@Data
public class QueryHisDeviceInfoReqVO {

    //网页版用户名，也就是设备号
    private String username;

}
