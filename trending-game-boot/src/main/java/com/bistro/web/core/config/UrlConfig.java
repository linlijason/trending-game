package com.bistro.web.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@ConfigurationProperties(prefix = "urls")
public class UrlConfig {

    private HashMap<String, String> gameLaunchUrl;

    public HashMap<String, String> getGameLaunchUrl() {
        return gameLaunchUrl;
    }

    public void setGameLaunchUrl(HashMap<String, String> gameLaunchUrl) {
        this.gameLaunchUrl = gameLaunchUrl;
    }
}
