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
 * @Description: 黑名单表
 * @Author: jeecg-boot
 * @Date:   2023-12-08
 * @Version: V1.0
 */
@Data
@TableName("tg_blacklist")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tg_blacklist对象", description="黑名单表")
public class TgBlacklist implements Serializable {
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
	/**被拉黑的用户id*/
	@Excel(name = "被拉黑的用户id", width = 15)
    @ApiModelProperty(value = "被拉黑的用户id")
    private String tgUserId;
	/**机器人id*/
	@Excel(name = "机器人id", width = 15)
    @ApiModelProperty(value = "机器人id")
    private String botId;
	/**禁用时间 1:1天 2:1周 3:1月 4:永久*/
	@Excel(name = "禁用时间 1:1天 2:1周 3:1月 4:永久", width = 15)
    @ApiModelProperty(value = "禁用时间 1:1天 2:1周 3:1月 4:永久")
    private Integer banType;
	/**禁用开始时间*/
	@Excel(name = "禁用开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "禁用开始时间")
    private Date banBeginTime;
	/**禁用截止时间*/
	@Excel(name = "禁用截止时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "禁用截止时间")
    private Date banEndTime;
	/**被拉黑的用户名*/
	@Excel(name = "被拉黑的用户名", width = 15)
    @ApiModelProperty(value = "被拉黑的用户名")
    private String tgUsername;
	/**聊天会话ID*/
	@Excel(name = "聊天会话ID", width = 15)
    @ApiModelProperty(value = "聊天会话ID")
    private String chatId;
}
