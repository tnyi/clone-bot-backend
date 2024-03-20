package org.jeecg.modules.telegram.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.telegram.config.BotConfig;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.handler.twoway.botmgr.AddBotCallbackHandler;
import org.jeecg.modules.telegram.handler.twoway.botmgr.DelBotCallbackHandler;
import org.jeecg.modules.telegram.mapper.TgBotMapper;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.jeecg.modules.telegram.vo.InlineButtonVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 机器人表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Service
public class TgBotServiceImpl extends ServiceImpl<TgBotMapper, TgBot> implements ITgBotService {

    @Resource
    private BotConfig botConfig;

    @Override
    public TgBot getByTgBotId(String botId) {
        return this.lambdaQuery().eq(TgBot::getTgBotId, botId).one();
    }

    @Override
    public TgBot getByBotName(String botName) {
        return this.lambdaQuery().eq(TgBot::getBotName, botName).one();
    }

    @Override
    public TgBot getByToken(String token) {
        return this.lambdaQuery().eq(TgBot::getBotToken, token).one();
    }

    @Override
    public ArrayList<List<InlineButtonVO>> twowayBotButton(String userId) {
        List<TgBot> botList = this.lambdaQuery().eq(TgBot::getAdminId, userId).list();
        InlineButtonVO.InlineButtonBuilder btnBuilder = InlineButtonVO.builder();
        if(botList.size() > 0) {
            for(TgBot bot : botList) {
                btnBuilder = btnBuilder
                        .addRow()
                        .addUrlButton("⚙️去设置" + bot.getBotName(), "https://t.me/"+bot.getBotName())
                        .addDataButton("\uD83D\uDDD1删除", bot.getId(), DelBotCallbackHandler.class);
//                        .addDataButton((bot.getServiceType()==1 ? "[免费版]" : "[专业版]") + " 点击切换", bot.getId(), ChangeServiceTypeCallbackHandler.class);
            }
        }
        btnBuilder = btnBuilder
                .addRow()
                .addDataButton("➕添加机器人", AddBotCallbackHandler.class)
                .addUrlButton("\uD83D\uDC65帮助交流", botConfig.getCommunicateGroup());
        return btnBuilder.build();
    }
}
