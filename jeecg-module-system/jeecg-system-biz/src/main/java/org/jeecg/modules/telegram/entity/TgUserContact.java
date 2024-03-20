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
 * @Description: 用户的联系人
 * @Author: jeecg-boot
 * @Date:   2023-12-06
 * @Version: V1.0
 */
@Data
@TableName("tg_user_contact")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tg_user_contact对象", description="用户的联系人")
public class TgUserContact implements Serializable {
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
	/**tg_user表id*/
	@Excel(name = "tg_user表id", width = 15)
    @ApiModelProperty(value = "tg_user表id")
    private java.lang.String userId;
	/**联系人tg id*/
	@Excel(name = "联系人tg id", width = 15)
    @ApiModelProperty(value = "联系人tg id")
    private java.lang.String contactTgId;
	/**联系人tg用户名*/
	@Excel(name = "联系人tg用户名", width = 15)
    @ApiModelProperty(value = "联系人tg用户名")
    private java.lang.String contactTgUsername;
	/**机器人id*/
	@Excel(name = "机器人id", width = 15)
    @ApiModelProperty(value = "机器人id")
    private java.lang.String botId;
}
