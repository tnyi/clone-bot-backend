package org.jeecg.modules.telegram.vo;

import lombok.Data;

import java.util.List;

/**
 * 根据当前网页版客户端信息查询历史网页版客户端信息
 */
@Data
public class QueryHisDeviceInfoResVO {

    //历史网页版设备号列表
    private List<String> deviceNoList;

    //网页端消息保存时长(1:不保存 2:1天 3:3天 4:1周 5:1个月 6:3个月 7:半年 8:永久保存
    private Integer saveTime;

}
