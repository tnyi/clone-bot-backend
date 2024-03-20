package org.jeecg.modules.telegram.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.telegram.entity.TgMsgTemplate;
import org.jeecg.modules.telegram.handler.agent.BackToMainHandler;
import org.jeecg.modules.telegram.handler.agent.msgtemplate.*;
import org.jeecg.modules.telegram.mapper.TgMsgTemplateMapper;
import org.jeecg.modules.telegram.service.ITgMsgTemplateService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 消息模板表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Service
public class TgMsgTemplateServiceImpl extends ServiceImpl<TgMsgTemplateMapper, TgMsgTemplate> implements ITgMsgTemplateService {

    @Override
    public TgMsgTemplate getByConfigName(String botId, String templateName) {
        return this.lambdaQuery().eq(TgMsgTemplate::getBotId, botId).eq(TgMsgTemplate::getTemplateName, templateName).one();
    }

    @Override
    public MsgContentVO tepmlateConfigMsg(String botId) {
        List<TgMsgTemplate> configList = this.lambdaQuery().eq(TgMsgTemplate::getBotId, botId).list();
        return tepmlateConfigMsg(configList);
    }

    @Override
    public MsgContentVO tepmlateConfigMsg(List<TgMsgTemplate> configList) {
        if(CollectionUtils.isEmpty(configList)) {
            configList = new ArrayList<>();
        }
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        for(TgMsgTemplate config : configList) {
            btnBuilder = btnBuilder.addRow()
                    .addDataButton("✍️" + config.getTemplateName(), config.getTemplateName(), ConfigMsgTemplateItemHandler.class, true)
                    .addDataButton("\uD83D\uDDD1删除", config.getTemplateName(), DelMsgTemplateHandler.class);
        }
        btnBuilder
                .addRow()
                .addDataButton("➕添加消息模板", AddMsgTemplateHandler.class)
                .addRow()
                .addDataButton("返回", BackToMainHandler.class, true);
        MsgContentVO vo = new MsgContentVO();
        vo.setButtonList(btnBuilder.build());
        return vo;
    }

    @Override
    public MsgContentVO configItemMsg(TgMsgTemplate config) {
        if(config == null) {
            config = new TgMsgTemplate();
        }
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder()
                .addRow()
                .addDataButton("✏️文本" + (StringUtils.isNotBlank(config.getTextMsg()) ? "✅" : "⬜️"), SetMsgTemplateTextHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewMsgTemplateTextHandler.class)
                .addDataButton("\uD83D\uDDD1删除", DelMsgTemplateTextHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDDBC媒体" + (StringUtils.isNotBlank(config.getMediaFile()) ? "✅" : "⬜️"), SetMsgTemplateMediaHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewMsgTemplateMediaHandler.class)
                .addDataButton("\uD83D\uDDD1删除", DelMsgTemplateMediaHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDCCD按钮" + (StringUtils.isNotBlank(config.getMsgButton()) ? "✅" : "⬜️"), SetMsgTemplateBtnHandler.class)
                .addDataButton("\uD83D\uDC40查看", ViewMsgTemplateBtnHandler.class)
                .addDataButton("\uD83D\uDDD1删除", DelMsgTemplateBtnHandler.class)
                .addRow()
                .addDataButton("\uD83D\uDC41预览消息", PreviewMsgTemplateHandler.class)
                .addRow()
                .addDataButton("返回", SetMsgTemplateHandler.class, true);
        MsgContentVO vo = new MsgContentVO();
        vo.setButtonList(btnBuilder.build());
        return vo;
    }

}
