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
 * @Description: 自动回复配置
 * @Author: jeecg-boot
 * @Date:   2023-11-17
 * @Version: V1.0
 */
@Data
@TableName("tg_auto_reply_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tg_auto_reply_config对象", description="自动回复配置")
public class TgAutoReplyConfig implements Serializable {
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
	/**配置条目名称*/
	@Excel(name = "配置条目名称", width = 15)
    @ApiModelProperty(value = "配置条目名称")
    private String configName;
	/**是否生效 1:已生效 2:未生效*/
	@Excel(name = "是否生效 1:已生效 2:未生效", width = 15)
    @ApiModelProperty(value = "是否生效 1:已生效 2:未生效")
    private Integer status;
	/**关键词组*/
	@Excel(name = "关键词组", width = 15)
    @ApiModelProperty(value = "关键词组")
    private String keywordList;
	/**回复文本*/
	@Excel(name = "回复文本", width = 15)
    @ApiModelProperty(value = "回复文本")
    private String textMsg;
	/**媒体文件路径*/
	@Excel(name = "媒体文件路径", width = 15)
    @ApiModelProperty(value = "媒体文件路径")
    private String mediaFile;
	/**消息按钮*/
	@Excel(name = "消息按钮", width = 15)
    @ApiModelProperty(value = "消息按钮")
    private String msgButton;
	/**匹配模式 1:模糊 2:精确*/
	@Excel(name = "匹配模式 1:模糊 2:精确", width = 15)
    @ApiModelProperty(value = "匹配模式 1:模糊 2:精确")
    private Integer matchType;
	/**仅管理员出发 1:是 2:否*/
	@Excel(name = "仅管理员出发 1:是 2:否", width = 15)
    @ApiModelProperty(value = "仅管理员出发 1:是 2:否")
    private Integer onlyForAdmin;
	/**机器人id*/
	@Excel(name = "机器人id", width = 15)
    @ApiModelProperty(value = "机器人id")
    private String botId;
	/**媒体类型1:图片 2:视频 3:文件*/
	@Excel(name = "媒体类型1:图片 2:视频 3:文件", width = 15)
    @ApiModelProperty(value = "媒体类型1:图片 2:视频 3:文件")
    private Integer mediaType;
}
