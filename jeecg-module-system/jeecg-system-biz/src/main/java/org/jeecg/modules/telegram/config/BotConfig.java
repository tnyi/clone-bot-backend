package org.jeecg.modules.telegram.config;

import lombok.Data;
import org.jeecg.modules.telegram.bot.TwoWayBot;
import org.jeecg.modules.telegram.util.BotUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 机器人相关配置初始化
 */
@Data
@Configuration
public class BotConfig {

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Value(value = "${bot.token}")
    private String token;

    @Value(value = "${bot.bot-username}")
    private String botUsername;

    @Value(value = "${bot.tech-support-url}")
    private String techSupportUrl;

    @Value(value = "${bot.tech-support-username}")
    private String techSupportUsername;

    @Value(value = "${bot.communicate-group}")
    private String communicateGroup;

    @Value(value = "${bot.use-proxy}")
    private Boolean useProxy;

    @Bean
    public void botConfigBean() {
        BotUtil.uploadpath = uploadpath;
        BotUtil.techSupportUrl = techSupportUrl;
        BotUtil.techSupportUsername = techSupportUsername;
        BotUtil.useProxy = useProxy;
        TwoWayBot.botToken = token;
        TwoWayBot.botUsername = botUsername;
    }
}
