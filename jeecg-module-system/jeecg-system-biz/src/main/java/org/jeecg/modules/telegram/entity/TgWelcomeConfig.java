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
 * @Description: 欢迎语配置
 * @Author: jeecg-boot
 * @Date:   2023-12-12
 * @Version: V1.0
 */
@Data
@TableName("tg_welcome_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tg_welcome_config对象", description="欢迎语配置")
public class TgWelcomeConfig implements Serializable {
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
	/**文本消息*/
	@Excel(name = "文本消息", width = 15)
    @ApiModelProperty(value = "文本消息")
    private String textMsg;
	/**媒体文件路径*/
	@Excel(name = "媒体文件路径", width = 15)
    @ApiModelProperty(value = "媒体文件路径")
    private String mediaFile;
	/**消息按钮*/
	@Excel(name = "消息按钮", width = 15)
    @ApiModelProperty(value = "消息按钮")
    private String msgButton;
	/**类型 1:机器人欢迎语 2:进群欢迎语*/
	@Excel(name = "类型 1:机器人欢迎语 2:进群欢迎语", width = 15)
    @ApiModelProperty(value = "类型 1:机器人欢迎语 2:进群欢迎语")
    private Integer configType;
	/**机器人id*/
	@Excel(name = "机器人id", width = 15)
    @ApiModelProperty(value = "机器人id")
    private String botId;
	/**媒体类型1:图片 2:视频 3:文件*/
	@Excel(name = "媒体类型1:图片 2:视频 3:文件", width = 15)
    @ApiModelProperty(value = "媒体类型1:图片 2:视频 3:文件")
    private Integer mediaType;
	/**按钮类型 1:消息按钮 2:底部按钮*/
	@Excel(name = "按钮类型 1:消息按钮 2:底部按钮", width = 15)
    @ApiModelProperty(value = "按钮类型 1:消息按钮 2:底部按钮")
    private Integer buttonType;
	/**配置名称*/
	@Excel(name = "配置名称", width = 15)
    @ApiModelProperty(value = "配置名称")
    private String configName;
	/**欢迎语保留时间，单位为秒（0表示永久）*/
	@Excel(name = "欢迎语保留时间，单位为秒（0表示永久）", width = 15)
    @ApiModelProperty(value = "欢迎语保留时间，单位为秒（0表示永久）")
    private Integer saveTime;
}
