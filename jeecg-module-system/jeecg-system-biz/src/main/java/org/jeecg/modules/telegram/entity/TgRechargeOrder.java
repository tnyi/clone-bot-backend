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
 * @Description: 用户充值订单表
 * @Author: jeecg-boot
 * @Date:   2023-12-04
 * @Version: V1.0
 */
@Data
@TableName("tg_recharge_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tg_recharge_order对象", description="用户充值订单表")
public class TgRechargeOrder implements Serializable {
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
	/**tg_user表id*/
	@Excel(name = "tg_user表id", width = 15)
    @ApiModelProperty(value = "tg_user表id")
    private String userId;
	/**金额*/
	@Excel(name = "金额", width = 15)
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
	/**收款地址*/
	@Excel(name = "收款地址", width = 15)
    @ApiModelProperty(value = "收款地址")
    private String receiptAddress;
	/**状态 1:待确认 2:成功 3:过期 4:取消*/
	@Excel(name = "状态 1:待确认 2:成功 3:过期 4:取消", width = 15)
    @ApiModelProperty(value = "状态 1:待确认 2:成功 3:过期 4:取消")
    private Integer status;
	/**充值到账时间*/
	@Excel(name = "充值到账时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "充值到账时间")
    private Date rechargeTime;
	/**订单过期时间*/
	@Excel(name = "订单过期时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "订单过期时间")
    private Date expireTime;
	/**订单取消时间*/
	@Excel(name = "订单取消时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "订单取消时间")
    private Date cancelTime;
	/**发起充值的会话ID*/
	@Excel(name = "发起充值的会话ID", width = 15)
    @ApiModelProperty(value = "发起充值的会话ID")
    private String chatId;
	/**发起充值的消息ID*/
	@Excel(name = "发起充值的消息ID", width = 15)
    @ApiModelProperty(value = "发起充值的消息ID")
    private String messageId;
}
