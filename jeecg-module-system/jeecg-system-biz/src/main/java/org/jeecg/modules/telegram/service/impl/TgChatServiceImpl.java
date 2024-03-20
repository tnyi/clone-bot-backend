package org.jeecg.modules.telegram.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.config.WebIMConfig;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.mapper.TgChatMapper;
import org.jeecg.modules.telegram.service.ITgChatService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.RegisterTGDTO;
import org.jeecg.modules.telegram.vo.TGChatUserDTO;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: 聊天信息表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Service
public class TgChatServiceImpl extends ServiceImpl<TgChatMapper, TgChat> implements ITgChatService {

    @Resource
    private WebIMConfig webIMConfig;

    @Override
    public void registerUserToWebIM(BaseBot botAgent, TgChat newChat) {
        RegisterTGDTO registerTGDTO = new RegisterTGDTO();
        registerTGDTO.setAutoLoing(false);
        registerTGDTO.setUserName(newChat.getChatId());
        registerTGDTO.setBotId(newChat.getBotId());

        GetChatMember getChatMember = GetChatMember.builder()
                .chatId(Long.parseLong(newChat.getChatId())) // 聊天的唯一标识符
                .userId(Long.parseLong(newChat.getAdminId())) // 目标用户的唯一标识符
                .build();
        ChatMember chatMember = botAgent.execute(getChatMember);
        registerTGDTO.setPassword(RandomUtil.randomString(6));
        registerTGDTO.setNickName(BotUtil.getFullname(chatMember.getUser()));

        List<TGChatUserDTO> chatUserList = new ArrayList<>();
        List<TgChat> tgChatList = lambdaQuery().eq(TgChat::getBotId, newChat.getBotId())
                                                .eq(TgChat::getChatType, 1)
                                                .ne(TgChat::getId, newChat.getId())
                                                .list();
        for(TgChat chat : tgChatList) {
            String adminId = chat.getAdminId();
            String chatId = chat.getChatId();
            getChatMember = GetChatMember.builder()
                    .chatId(Long.parseLong(chatId)) // 聊天的唯一标识符
                    .userId(Long.parseLong(adminId)) // 目标用户的唯一标识符
                    .build();
            try {
                chatMember = botAgent.execute(getChatMember);
                if (chatMember != null) {
                    TGChatUserDTO userDTO = new TGChatUserDTO();
                    userDTO.setUserName(chat.getChatId());
                    userDTO.setNickName(BotUtil.getFullname(chatMember.getUser()));
                    chatUserList.add(userDTO);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        registerTGDTO.setChatUserList(chatUserList);
        String retJSON = HttpUtil.createPost(webIMConfig.getDomain() + "/autoRegisterFromTG")
                            .body(JSON.toJSONString(registerTGDTO), "application/json")
                            .execute().body();
        JSONObject retObj = JSON.parseObject(retJSON);
        Integer code = retObj.getInteger("code");
        if(code != 200) {
            throw new JeecgBootException("web im 同步用户失败");
        }
    }

}
