package org.jeecg.modules.telegram.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.agent.ViewEmptyBtnHandler;
import org.jeecg.modules.telegram.handler.agent.autoreply.*;
import org.jeecg.modules.telegram.mapper.TgAutoReplyConfigMapper;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 自动回复配置
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Service
public class TgAutoReplyConfigServiceImpl extends ServiceImpl<TgAutoReplyConfigMapper, TgAutoReplyConfig> implements ITgAutoReplyConfigService {

    @Override
    public TgAutoReplyConfig getByConfigName(String botId, String configName) {
        return this.lambdaQuery().eq(TgAutoReplyConfig::getBotId, botId).eq(TgAutoReplyConfig::getConfigName, configName).one();
    }

    @Override
    public MsgContentVO autoReplyConfigMsg(String botId) {
        List<TgAutoReplyConfig> configList = this.lambdaQuery().eq(TgAutoReplyConfig::getBotId, botId).list();
        return autoReplyConfigMsg(configList);
    }

    @Override
    public MsgContentVO autoReplyConfigMsg(List<TgAutoReplyConfig> configList) {
        if(CollectionUtils.isEmpty(configList)) {
            configList = new ArrayList<>();
        }
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        for(TgAutoReplyConfig config : configList) {
            StringBuilder statStr = new StringBuilder();
            if(StringUtils.isBlank(config.getKeywordList())) {
                statStr.append("⚠️");
            } else {
                statStr.append("\uD83D\uDCCC");
                if(StringUtils.isBlank(config.getTextMsg())) {
                    statStr.append("✏️");
                }
                if(StringUtils.isBlank(config.getMediaFile())) {
                    statStr.append("\uD83D\uDDBC");
                }
                if(StringUtils.isBlank(config.getMsgButton())) {
                    statStr.append("\uD83D\uDCCD");
                }
                if(config.getMatchType() == 1) {
                    statStr.append("\uD83D\uDD18");
                } else {
                    statStr.append("\uD83D\uDD0D");
                }
                if(config.getOnlyForAdmin() == 1) {
                    statStr.append("\uD83E\uDDD1\u200D\uD83D\uDCBC");
                }
            }
            btnBuilder = btnBuilder.addRow()
                    .addDataButton("\uD83E\uDD16" + config.getConfigName(), config.getConfigName(), ConfigAutoReplyItemHandler.class, true)
                    .addDataButton(statStr.toString(), ViewReplyConfigHandler.class)
                    .addDataButton("\uD83D\uDDD1删除", config.getConfigName(), DelAutoReplyHandler.class);
        }
        btnBuilder
                .addRow()
                .addDataButton("➕添加自动回复", AddAutoReplyHandler.class)
                .addRow()
                .addDataButton("返回", BackToMainHandler.class, true);
        MsgContentVO vo = new MsgContentVO();
        vo.setButtonList(btnBuilder.build());
        return vo;
    }

    @Override
    public MsgContentVO configItemMsg(TgAutoReplyConfig config) {
        if(config == null) {
            config = new TgAutoReplyConfig();
        }
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder()
                .addRow()
                .addDataButton("\uD83D\uDCCC设置关键词组" + (StringUtils.isNotBlank(config.getKeywordList()) ? "✅" : "⬜️"), SetAutoReplyKeywordHandler.class)
                .addRow()
                .addDataButton("✏️文本" + (StringUtils.isNotBlank(config.getTextMsg()) ? "✅" : "⬜️"), SetAutoReplyTextHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewAutoReplyTextHandler.class)
                .addDataButton("\uD83D\uDDD1删除", DelAutoReplyTextHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDDBC媒体" + (StringUtils.isNotBlank(config.getMediaFile()) ? "✅" : "⬜️"), SetAutoReplyMediaHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewAutoReplyMediaHandler.class)
                .addDataButton("\uD83D\uDDD1删除", DelAutoReplyMediaHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDCCD按钮" + (StringUtils.isNotBlank(config.getMsgButton()) ? "✅" : "⬜️"), SetAutoReplyBtnHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewAutoReplyBtnHandler.class)
                .addDataButton("\uD83D\uDDD1删除", DelAutoReplyBtnHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDD0D匹配模式：" + (config.getMatchType()==2 ? "精确" : "包含") + "[点击切换]", SetAutoReplyMatchTypeHandler.class)
                .addDataButton("\uD83E\uDDD1\u200D\uD83D\uDCBC仅管理员可触发" + (config.getOnlyForAdmin()==1 ? "✅" : "⬜️"), SetAutoReplyOnlyForAdminHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDC41预览消息", PreviewAutoReplyMsgHandler.class)
                .addRow()
                .addDataButton("返回", SetAutoReplyHandler.class, true);
        MsgContentVO vo = new MsgContentVO();
        vo.setButtonList(btnBuilder.build());
        return vo;
    }

}
