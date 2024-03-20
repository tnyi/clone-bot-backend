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
 * @Description: 机器人管理员表
 * @Author: jeecg-boot
 * @Date:   2023-12-08
 * @Version: V1.0
 */
@Data
@TableName("tg_bot_admin")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tg_bot_admin对象", description="机器人管理员表")
public class TgBotAdmin implements Serializable {
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
	/**管理员id*/
	@Excel(name = "管理员id", width = 15)
    @ApiModelProperty(value = "管理员id")
    private String adminId;
	/**管理员用户名*/
	@Excel(name = "管理员用户名", width = 15)
    @ApiModelProperty(value = "管理员用户名")
    private String adminUsername;
	/**机器人ID*/
	@Excel(name = "机器人ID", width = 15)
    @ApiModelProperty(value = "机器人ID")
    private String botId;
	/**管理员昵称*/
	@Excel(name = "管理员昵称", width = 15)
    @ApiModelProperty(value = "管理员昵称")
    private String adminNickname;
	/**会话id*/
	@Excel(name = "会话id", width = 15)
    @ApiModelProperty(value = "会话id")
    private String chatId;
}
