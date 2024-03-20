package org.jeecg.modules.telegram.handler.agent.autoreply;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.telegram.bot.BaseBot;
import org.jeecg.modules.telegram.constant.BotRedisConstant;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.handler.BaseBotMsgHandler;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;
import org.jeecg.modules.telegram.service.ITgCallbackDataService;
import org.jeecg.modules.telegram.util.BotUtil;
import org.jeecg.modules.telegram.vo.MsgContentVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 处理代理机器人的"设置自动回复-媒体文件-成功"指令
 */
@Slf4j
@Service
public class SetAutoReplyMediaOKHandler implements BaseBotMsgHandler {

    @Resource
    private ITgCallbackDataService callbackDataService;

    @Resource
    private ITgAutoReplyConfigService autoReplyConfigService;

    @Resource
    private SetAutoReplyMediaHandler setAutoReplyMediaHandler;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Override
    public void process(BaseBot botAgent, Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        Video video = message.getVideo();
        List<PhotoSize> photoList = message.getPhoto();
        Document document = message.getDocument();
        if(video==null && (photoList==null || photoList.size()==0) && document==null) {
            setAutoReplyMediaHandler.process(botAgent, update);
            return;
        }

        String path = null;
        int mediaType = 1;
        try {
            String fileId = null;
            if (video != null) {
                fileId = video.getFileId();
                mediaType = 2;
            }

            if (photoList!=null && photoList.size()>0) {
                PhotoSize photoSize = photoList.get(photoList.size()-1);
                fileId = photoSize.getFileId();
                mediaType = 1;
            }

            if (document != null) {
                fileId = document.getFileId();
                mediaType = 3;
            }

            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);
            File tgFile = botAgent.execute(getFile);
            String filePath = tgFile.getFilePath();
            String suffix = tgFile.getFilePath().substring(tgFile.getFilePath().lastIndexOf("."));
            java.io.File downloadedFile = botAgent.downloadFile(filePath);
            path = "/" + botAgent.getDbBotId() + "/welcome/" + System.currentTimeMillis() + suffix;
            String destFilePath = uploadpath + path;
            FileUtil.copy(downloadedFile.getAbsolutePath(), destFilePath, true);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JeecgBootException(e.getMessage(), e);
        }

        String configName = callbackDataService.get(BotRedisConstant.EDIT_DATA_ITEM_CHAT_ID + chatId);
        TgAutoReplyConfig replyConfig = autoReplyConfigService.getByConfigName(botAgent.getDbBotId(), configName);
        String oldFilePath = replyConfig.getMediaFile();
        replyConfig.setMediaFile(path);
        replyConfig.setMediaType(mediaType);
        autoReplyConfigService.updateById(replyConfig);
        java.io.File file = new java.io.File(uploadpath + oldFilePath);
        if(file.exists()) {
            file.delete(); //删除旧文件
        }

        String keywordTips = BotUtil.autoReplyKeywordTips(replyConfig);
        String msg = "\uD83C\uDF89 欢迎语设置，"+keywordTips+"\n" +
                        "\n" +
                        "✅ 设置媒体文件成功";
        BotUtil.delTxtRelyHandler(botAgent.getDbBotId(), message.getChatId());
        MsgContentVO contentVO = autoReplyConfigService.configItemMsg(replyConfig);
        botAgent.sendMsgWithData(message.getChatId(), msg, contentVO.getButtonList());
    }

}
