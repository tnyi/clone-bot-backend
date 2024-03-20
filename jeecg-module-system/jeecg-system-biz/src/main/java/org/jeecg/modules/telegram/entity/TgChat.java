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
 * @Description: 聊天信息表
 * @Author: jeecg-boot
 * @Date:   2023-12-06
 * @Version: V1.0
 */
@Data
@TableName("tg_chat")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tg_chat对象", description="聊天信息表")
public class TgChat implements Serializable {
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
	/**聊天ID*/
	@Excel(name = "聊天ID", width = 15)
    @ApiModelProperty(value = "聊天ID")
    private String chatId;
	/**状态1:启用 2:未启用*/
	@Excel(name = "状态1:启用 2:未启用", width = 15)
    @ApiModelProperty(value = "状态1:启用 2:未启用")
    private Integer status;
	/**聊天群中机器人的管理人ID*/
	@Excel(name = "聊天群中机器人的管理人ID", width = 15)
    @ApiModelProperty(value = "聊天群中机器人的管理人ID")
    private String adminId;
	/**机器人ID*/
	@Excel(name = "机器人ID", width = 15)
    @ApiModelProperty(value = "机器人ID")
    private String botId;
	/**类型 1:单独对话 2:群组*/
	@Excel(name = "类型 1:单独对话 2:群组", width = 15)
    @ApiModelProperty(value = "类型 1:单独对话 2:群组")
    private Integer chatType;
	/**机器人是否是管理员 1:是 2:否*/
	@Excel(name = "机器人是否是管理员 1:是 2:否", width = 15)
    @ApiModelProperty(value = "机器人是否是管理员 1:是 2:否")
    private Integer botIsAdmin;
	/**消息接收人tg id*/
	@Excel(name = "消息接收人tg id", width = 15)
    @ApiModelProperty(value = "消息接收人tg id")
    private String msgRecieverId;
	/**是否是机器人管理会话 1:是 2:否*/
	@Excel(name = "是否是机器人管理会话 1:是 2:否", width = 15)
    @ApiModelProperty(value = "是否是机器人管理会话 1:是 2:否")
    private Integer adminChat;
	/**聊天群中机器人的管理人账号*/
	@Excel(name = "聊天群中机器人的管理人账号", width = 15)
    @ApiModelProperty(value = "聊天群中机器人的管理人账号")
    private String adminUsername;
	/**最近一次发送消息的时间*/
	@Excel(name = "最近一次发送消息的时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最近一次发送消息的时间")
    private Date lastMsgTime;
}
