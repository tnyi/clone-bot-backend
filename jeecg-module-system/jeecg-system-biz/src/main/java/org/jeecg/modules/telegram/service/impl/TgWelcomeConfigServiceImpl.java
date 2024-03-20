package org.jeecg.modules.telegram.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.agent.groupwelcome.*;
import org.jeecg.modules.telegram.handler.agent.welcome.*;
import org.jeecg.modules.telegram.mapper.TgWelcomeConfigMapper;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 欢迎语配置
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Service
public class TgWelcomeConfigServiceImpl extends ServiceImpl<TgWelcomeConfigMapper, TgWelcomeConfig> implements ITgWelcomeConfigService {

    @Override
    public TgWelcomeConfig getBotWelcomeByConfigId(String configId) {
        return lambdaQuery().eq(TgWelcomeConfig::getConfigType, 1).eq(TgWelcomeConfig::getId, configId).one();
    }

    @Override
    public TgWelcomeConfig getGroupWelcomeById(String configId) {
        return lambdaQuery().eq(TgWelcomeConfig::getConfigType, 2).eq(TgWelcomeConfig::getId, configId).one();
    }

    @Override
    public MsgContentVO welcomeConfigMsg(String configId) {
        TgWelcomeConfig config = this.getBotWelcomeByConfigId(configId);
        return welcomeConfigMsg(config);
    }

    @Override
    public MsgContentVO welcomeConfigMsg(TgWelcomeConfig config) {
        if(config == null) {
            config = new TgWelcomeConfig();
        }
        String btnType = "\uD83D\uDCCD";
        if(StringUtils.isNotBlank(config.getMsgButton()) && config.getButtonType()!=null) {
            btnType = (config.getButtonType()==1?"\uD83D\uDCCD消息":"\uD83D\uDD79底部");
        }
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder()
                .addRow()
                .addDataButton("✏️文本" + (StringUtils.isNotBlank(config.getTextMsg()) ? "✅" : "⬜️"), SetWelcomeTextHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewWelcomeTextHandler.class)
                .addDataButton("\uD83D\uDDD1️删除", DelWelcomeTextHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDDBC️媒体" + (StringUtils.isNotBlank(config.getMediaFile()) ? "✅" : "⬜️"), SetWelcomeMediaHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewWelcomeMediaHandler.class)
                .addDataButton("\uD83D\uDDD1️删除", DelWelcomeMediaHandler.class)
                .addRow()
                .addDataButton(btnType + "按钮" + (StringUtils.isNotBlank(config.getMsgButton()) ? "✅" : "⬜️"), SelectWelcomeBtnTypeHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewWelcomeBtnHandler.class)
                .addDataButton("\uD83D\uDDD1️删除", DelWelcomeBtnHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDC41预览消息", PreviewWelcomeMsgHandler.class)
                .addRow()
                .addDataButton("返回", WelcomeListHandler.class);
        MsgContentVO vo = new MsgContentVO();
        vo.setButtonList(btnBuilder.build());
        return vo;
    }

    @Override
    public MsgContentVO groupWelcomeConfigMsg(String configId) {
        TgWelcomeConfig config = this.getGroupWelcomeById(configId);
        return groupWelcomeConfigMsg(config);
    }

    @Override
    public MsgContentVO groupWelcomeConfigMsg(TgWelcomeConfig config) {
        if(config == null) {
            config = new TgWelcomeConfig();
        }
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder()
                .addRow()
                .addDataButton("✏️文本" + (StringUtils.isNotBlank(config.getTextMsg()) ? "✅" : "⬜️"), SetGroupWelcomeTextHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewGroupWelcomeTextHandler.class)
                .addDataButton("\uD83D\uDDD1删除", DelGroupWelcomeTextHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDDBC媒体" + (StringUtils.isNotBlank(config.getMediaFile()) ? "✅" : "⬜️"), SetGroupWelcomeMediaHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewGroupWelcomeMediaHandler.class)
                .addDataButton("\uD83D\uDDD1删除", DelGroupWelcomeMediaHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDCCD按钮" + (StringUtils.isNotBlank(config.getMsgButton()) ? "✅" : "⬜️"), SetGroupWelcomeBtnHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewGroupWelcomeBtnHandler.class)
                .addDataButton("\uD83D\uDDD1删除", DelGroupWelcomeBtnHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDC41预览消息", PreviewGroupWelcomeMsgHandler.class)
                .addRow()
                .addDataButton("返回", GroupWelcomeListHandler.class, true);
        MsgContentVO vo = new MsgContentVO();
        vo.setButtonList(btnBuilder.build());
        return vo;
    }

    @Override
    public MsgContentVO welcomeConfigListMsg(String botId) {
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        List<TgWelcomeConfig> configList = this.lambdaQuery().eq(TgWelcomeConfig::getBotId, botId).eq(TgWelcomeConfig::getConfigType, 1).list();
        for(TgWelcomeConfig config : configList) {
            StringBuilder statStr = new StringBuilder();
            if(StringUtils.isBlank(config.getTextMsg())) {
                statStr.append("⚠️");
            } else {
                if(StringUtils.isNotBlank(config.getTextMsg())) {
                    statStr.append("✏️");
                }
                if(StringUtils.isNotBlank(config.getMediaFile())) {
                    statStr.append("\uD83D\uDDBC");
                }
                if(StringUtils.isNotBlank(config.getMsgButton())) {
                    if(config.getButtonType() == 1) {
                        statStr.append("\uD83D\uDCCD");
                    } else {
                        statStr.append("\uD83D\uDD79");
                    }
                }
            }
            btnBuilder.addRow()
                    .addDataButton("\uD83C\uDF89" + config.getConfigName(), config.getId(), SetWelcomeHandler.class)
                    .addDataButton(statStr.toString(), ViewWelcomeConfigHandler.class)
                    .addDataButton("\uD83D\uDDD1删除", config.getId(), DelWelcomeHandler.class);
        }
        btnBuilder.addRow().addDataButton("➕添加欢迎语", AddWelcomeItemHandler.class, true);
        btnBuilder.addRow().addDataButton("返回", BackToMainHandler.class, true);
        MsgContentVO vo = new MsgContentVO();
        vo.setButtonList(btnBuilder.build());
        return vo;
    }

    @Override
    public MsgContentVO groupWelcomeConfigListMsg(String botId) {
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        List<TgWelcomeConfig> configList = this.lambdaQuery().eq(TgWelcomeConfig::getBotId, botId).eq(TgWelcomeConfig::getConfigType, 2).list();
        for(TgWelcomeConfig config : configList) {
            StringBuilder statStr = new StringBuilder();
            if(StringUtils.isBlank(config.getTextMsg())) {
                statStr.append("⚠️");
            } else {
                if(StringUtils.isNotBlank(config.getTextMsg())) {
                    statStr.append("✏️");
                }
                if(StringUtils.isNotBlank(config.getMediaFile())) {
                    statStr.append("\uD83D\uDDBC");
                }
                if(StringUtils.isNotBlank(config.getMsgButton())) {
                    statStr.append("\uD83D\uDCCD");
                }
            }
            btnBuilder.addRow()
                    .addDataButton("\uD83C\uDF8A" + config.getConfigName(), config.getId(), SetGroupWelcomeHandler.class)
                    .addDataButton(statStr.toString(), ViewGroupWelcomeConfigHandler.class)
                    .addDataButton("\uD83D\uDDD1删除", config.getId(), DelGroupWelcomeHandler.class)
                    .addDataButton("时间[" + (config.getSaveTime() == 0 ? "永久" : config.getSaveTime()+"秒") + "]" , config.getId(), SetGroupWelcomeSaveTimeHandler.class);
        }
        btnBuilder.addRow().addDataButton("➕添加欢迎语", AddGroupWelcomeItemHandler.class, true);
        btnBuilder.addRow().addDataButton("返回", BackToMainHandler.class, true);
        MsgContentVO vo = new MsgContentVO();
        vo.setButtonList(btnBuilder.build());
        return vo;
    }

}
