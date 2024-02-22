package com.bistro.web.core.config;

import com.bistro.module.merchant.domain.MerchantInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "merchant")
public class MerchantConfig {

    private Map<String, MerchantInfo> config;

    public Map<String, MerchantInfo> getConfig() {
        return config;
    }

    public void setConfig(Map<String, MerchantInfo> config) {
        this.config = config;
    }
}
