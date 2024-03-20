package org.jeecg.modules.telegram.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.service.ITgBotService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class CheckBotExpireTimeJob {

    @Resource
    private ITgBotService botService;

    /**
     * 每1分钟检查有没有到期的专业版机器人
     */
    @Scheduled(cron = "* */1 * * * ?")
	public void execute() {
        // 查找已经到期的专业版机器人
        List<TgBot> botList = botService.lambdaQuery().eq(TgBot::getServiceType, 2).lt(TgBot::getExpireTime, new Date()).list();
        for(TgBot bot : botList) {
            bot.setServiceType(1);
            botService.updateById(bot);
        }
    }
}
