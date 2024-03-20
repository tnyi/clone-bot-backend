package org.jeecg.modules.telegram.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 用户资金记录表
 * @Author: jeecg-boot
 * @Date:   2023-12-04
 * @Version: V1.0
 */
@Data
@TableName("tg_user_cash_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tg_user_cash_log对象", description="用户资金记录表")
public class TgUserCashLog implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**tg_user表 id*/
	@Excel(name = "tg_user表 id", width = 15)
    @ApiModelProperty(value = "tg_user表 id")
    private String userId;
	/**资金金额*/
	@Excel(name = "资金金额", width = 15)
    @ApiModelProperty(value = "资金金额")
    private BigDecimal cashAmount;
	/**当前余额*/
	@Excel(name = "当前余额", width = 15)
    @ApiModelProperty(value = "当前余额")
    private BigDecimal balance;
	/**资金类型 1:充值 2:购买专业版 3:提现*/
	@Excel(name = "资金类型 1:充值 2:购买专业版 3:提现", width = 15)
    @ApiModelProperty(value = "资金类型 1:充值 2:购买专业版 3:提现")
    private Integer cashType;
}
