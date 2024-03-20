package org.jeecg.modules.telegram.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Web IM相关配置
 */
@Data
@Configuration
public class WebIMConfig {

    @Value(value = "${web-im.domain}")
    private String domain;

    @Value(value = "${web-im.frontUrl}")
    private String frontUrl;

}
