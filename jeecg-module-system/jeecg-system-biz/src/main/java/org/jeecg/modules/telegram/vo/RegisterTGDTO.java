package org.jeecg.modules.telegram.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("用户TG注册 DTO")
public class RegisterTGDTO {

    @Length(max = 64,message = "用户名不能大于64字符")
    @NotEmpty(message="用户名不可为空")
    @ApiModelProperty(value = "用户名")
    private String userName;

    @Length(min=5,max = 20,message = "密码长度必须在5-20个字符之间")
    @NotEmpty(message="用户密码不可为空")
    @ApiModelProperty(value = "用户密码")
    private String password;

    @Length(max = 64,message = "昵称不能大于64字符")
    @NotEmpty(message="用户昵称不可为空")
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    //机器人ID
    private String botId;

    //是否自动登录
    private Boolean autoLoing = true;

    //该机器人的多个用户自动加好友
    private List<TGChatUserDTO> chatUserList = new ArrayList<>();

}
