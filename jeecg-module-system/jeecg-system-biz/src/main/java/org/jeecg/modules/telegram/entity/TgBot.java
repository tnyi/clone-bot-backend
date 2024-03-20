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
 * @Description: 机器人表
 * @Author: jeecg-boot
 * @Date:   2023-12-04
 * @Version: V1.0
 */
@Data
@TableName("tg_bot")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tg_bot对象", description="机器人表")
public class TgBot implements Serializable {
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
	/**tg机器人token*/
	@Excel(name = "tg机器人token", width = 15)
    @ApiModelProperty(value = "tg机器人token")
    private String botToken;
	/**tg机器人管理员ID*/
	@Excel(name = "tg机器人管理员ID", width = 15)
    @ApiModelProperty(value = "tg机器人管理员ID")
    private String adminId;
	/**tg机器人名称*/
	@Excel(name = "tg机器人名称", width = 15)
    @ApiModelProperty(value = "tg机器人名称")
    private String botName;
	/**服务类型1:免费版 2:专业版*/
	@Excel(name = "服务类型1:免费版 2:专业版", width = 15)
    @ApiModelProperty(value = "服务类型1:免费版 2:专业版")
    private Integer serviceType;
	/**聊天模式1:选择模式 2:锁定模式 3:智能模式*/
	@Excel(name = "聊天模式1:选择模式 2:锁定模式 3:智能模式", width = 15)
    @ApiModelProperty(value = "聊天模式1:选择模式 2:锁定模式 3:智能模式")
    private Integer chatMode;
	/**消息自动回复 1:启动 2:关闭*/
	@Excel(name = "消息自动回复 1:启动 2:关闭", width = 15)
    @ApiModelProperty(value = "消息自动回复 1:启动 2:关闭")
    private Integer autoReplyMsg;
	/**同时触发多个回复1:开启 2:关闭*/
	@Excel(name = "同时触发多个回复1:开启 2:关闭", width = 15)
    @ApiModelProperty(value = "同时触发多个回复1:开启 2:关闭")
    private Integer mutiplyReply;
	/**显示消息网页预览 1:开启 2:关闭*/
	@Excel(name = "显示消息网页预览 1:开启 2:关闭", width = 15)
    @ApiModelProperty(value = "显示消息网页预览 1:开启 2:关闭")
    private Integer displayWebPage;
	/**欢迎语的触发提示1:开启 2:关闭*/
	@Excel(name = "欢迎语的触发提示1:开启 2:关闭", width = 15)
    @ApiModelProperty(value = "欢迎语的触发提示1:开启 2:关闭")
    private Integer welcomeMsgTips;
	/**关键词组触发提示1:开启 2:关闭*/
	@Excel(name = "关键词组触发提示1:开启 2:关闭", width = 15)
    @ApiModelProperty(value = "关键词组触发提示1:开启 2:关闭")
    private Integer keywordTips;
	/**消息发送成功提示1:开启 2:关闭*/
	@Excel(name = "消息发送成功提示1:开启 2:关闭", width = 15)
    @ApiModelProperty(value = "消息发送成功提示1:开启 2:关闭")
    private Integer sendOkTips;
	/**定时清理成功提示1:永不 2:3秒 3:5秒*/
	@Excel(name = "定时清理成功提示1:永不 2:3秒 3:5秒", width = 15)
    @ApiModelProperty(value = "定时清理成功提示1:永不 2:3秒 3:5秒")
    private Integer clearOkTips;
	/**群关键词自动回复1:开启 2:关闭*/
	@Excel(name = "群关键词自动回复1:开启 2:关闭", width = 15)
    @ApiModelProperty(value = "群关键词自动回复1:开启 2:关闭")
    private Integer groupAutoReply;
	/**进群欢迎自动回复1:开启 2:关闭*/
	@Excel(name = "进群欢迎自动回复1:开启 2:关闭", width = 15)
    @ApiModelProperty(value = "进群欢迎自动回复1:开启 2:关闭")
    private Integer joinAutoReply;
	/**删除上条进群欢迎1:开启 2:关闭*/
	@Excel(name = "删除上条进群欢迎1:开启 2:关闭", width = 15)
    @ApiModelProperty(value = "删除上条进群欢迎1:开启 2:关闭")
    private Integer delLastJoin;
	/**底部按钮*/
	@Excel(name = "底部按钮", width = 15)
    @ApiModelProperty(value = "底部按钮")
    private String bottomButton;
	/**机器人的tg id*/
	@Excel(name = "机器人的tg id", width = 15)
    @ApiModelProperty(value = "机器人的tg id")
    private String tgBotId;
	/**指令集*/
	@Excel(name = "指令集", width = 15)
    @ApiModelProperty(value = "指令集")
    private String commandList;
	/**网页端发送消息通知管理员（1:是 2:否）*/
	@Excel(name = "网页端发送消息通知管理员（1:是 2:否）", width = 15)
    @ApiModelProperty(value = "网页端发送消息通知管理员（1:是 2:否）")
    private Integer webMsgNotifyAdmin;
	/**网页端消息保存时长(1:不保存 2:1天 3:3天 4:1周 5:1个月 6:3个月 7:半年 8:永久保存*/
	@Excel(name = "网页端消息保存时长(1:不保存 2:1天 3:3天 4:1周 5:1个月 6:3个月 7:半年 8:永久保存", width = 15)
    @ApiModelProperty(value = "网页端消息保存时长(1:不保存 2:1天 3:3天 4:1周 5:1个月 6:3个月 7:半年 8:永久保存")
    private Integer webMsgSaveTime;
	/**网页版登录token*/
	@Excel(name = "网页版登录token", width = 15)
    @ApiModelProperty(value = "网页版登录token")
    private String webLoginToken;
	/**tg机器人管理员账号*/
	@Excel(name = "tg机器人管理员账号", width = 15)
    @ApiModelProperty(value = "tg机器人管理员账号")
    private String adminUsername;
	/**自动回复带目标源 1:是 2:否*/
	@Excel(name = "自动回复带目标源 1:是 2:否", width = 15)
    @ApiModelProperty(value = "自动回复带目标源 1:是 2:否")
    private Integer replyWithTarget;
	/**自动回复次数*/
	@Excel(name = "自动回复次数", width = 15)
    @ApiModelProperty(value = "自动回复次数")
    private Integer replyCount;
	/**专业版到期时间*/
	@Excel(name = "专业版到期时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "专业版到期时间")
    private Date expireTime;
	/**是否已试用 1:未试用 2:已试用*/
	@Excel(name = "是否已试用 1:未试用 2:已试用", width = 15)
    @ApiModelProperty(value = "是否已试用 1:未试用 2:已试用")
    private Integer trialStatu;
	/**试用开始时间*/
	@Excel(name = "试用开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "试用开始时间")
    private Date trialBeginTime;
	/**试用截止时间*/
	@Excel(name = "试用截止时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "试用截止时间")
    private Date trialEndTime;
}
