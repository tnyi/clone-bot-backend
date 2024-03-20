package org.jeecg.modules.telegram.vo;

import lombok.Data;

/**
 * 删除黑名单
 */
@Data
public class DelBlacklistVO {

    private int pageSize;

    private int pageNo;

    private String userId;

}
