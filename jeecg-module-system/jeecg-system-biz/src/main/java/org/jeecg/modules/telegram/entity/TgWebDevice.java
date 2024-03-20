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
 * @Description: 网页版登录设备表
 * @Author: jeecg-boot
 * @Date:   2023-11-28
 * @Version: V1.0
 */
@Data
@TableName("tg_web_device")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tg_web_device对象", description="网页版登录设备表")
public class TgWebDevice implements Serializable {
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
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
    private String username;
	/**登录ip*/
	@Excel(name = "登录ip", width = 15)
    @ApiModelProperty(value = "登录ip")
    private String loginIp;
	/**归属地*/
	@Excel(name = "归属地", width = 15)
    @ApiModelProperty(value = "归属地")
    private String location;
	/**运营商*/
	@Excel(name = "运营商", width = 15)
    @ApiModelProperty(value = "运营商")
    private String provider;
	/**设备类型 1:电脑 2:手机 3:平板*/
	@Excel(name = "设备类型 1:电脑 2:手机 3:平板", width = 15)
    @ApiModelProperty(value = "设备类型 1:电脑 2:手机 3:平板")
    private Integer deviceType;
	/**浏览器*/
	@Excel(name = "浏览器", width = 15)
    @ApiModelProperty(value = "浏览器")
    private String browser;
	/**机器人ID*/
	@Excel(name = "机器人ID", width = 15)
    @ApiModelProperty(value = "机器人ID")
    private String botId;
	/**状态 1:在线 2:离线*/
	@Excel(name = "状态 1:在线 2:离线", width = 15)
    @ApiModelProperty(value = "状态 1:在线 2:离线")
    private Integer status;
	/**设备标识*/
	@Excel(name = "设备标识", width = 15)
    @ApiModelProperty(value = "设备标识")
    private String deviceNo;
}
