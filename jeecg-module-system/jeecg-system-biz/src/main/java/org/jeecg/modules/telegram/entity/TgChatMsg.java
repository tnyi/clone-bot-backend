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
 * @Description: 用户聊天信息表
 * @Author: jeecg-boot
 * @Date:   2023-12-06
 * @Version: V1.0
 */
@Data
@TableName("tg_chat_msg")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tg_chat_msg对象", description="用户聊天信息表")
public class TgChatMsg implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**机器人id*/
	@Excel(name = "机器人id", width = 15)
    @ApiModelProperty(value = "机器人id")
    private java.lang.String botId;
	/**机器人所属用户id（tg_user表id）*/
	@Excel(name = "机器人所属用户id（tg_user表id）", width = 15)
    @ApiModelProperty(value = "机器人所属用户id（tg_user表id）")
    private java.lang.String userId;
	/**产生该消息的会话id*/
	@Excel(name = "产生该消息的会话id", width = 15)
    @ApiModelProperty(value = "产生该消息的会话id")
    private java.lang.String fromChatId;
	/**消息内容*/
	@Excel(name = "消息内容", width = 15)
    @ApiModelProperty(value = "消息内容")
    private java.lang.String msgContent;
	/**类型1:管理员发给用户 2:用户发给管理员*/
	@Excel(name = "类型1:管理员发给用户 2:用户发给管理员", width = 15)
    @ApiModelProperty(value = "类型1:管理员发给用户 2:用户发给管理员")
    private java.lang.Integer msgType;
	/**发送者tg user id*/
	@Excel(name = "发送者tg user id", width = 15)
    @ApiModelProperty(value = "发送者tg user id")
    private java.lang.String fromUserId;
	/**接收者tg user id*/
	@Excel(name = "接收者tg user id", width = 15)
    @ApiModelProperty(value = "接收者tg user id")
    private java.lang.String toUserId;
	/**接收的会话id*/
	@Excel(name = "接收的会话id", width = 15)
    @ApiModelProperty(value = "接收的会话id")
    private java.lang.String toChatId;
	/**内容类型 1:文本 2:图片*/
	@Excel(name = "内容类型 1:文本 2:图片", width = 15)
    @ApiModelProperty(value = "内容类型 1:文本 2:图片")
    private java.lang.Integer contentType;
}
