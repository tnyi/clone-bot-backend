package org.jeecg.modules.telegram.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("TG聊天信息中用户 DTO")
public class TGChatUserDTO {

    //对应tg chat id
    private String userName;

    //对应tg chat中的管理员username
    private String nickName;

}
