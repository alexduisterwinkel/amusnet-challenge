package nl.alex.amusnet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    @Value("${url.players}")
    private String playerUrl;

    @Value("${url.activity}")
    private String activityUrl;

    public String getPlayerUrl() {
        return playerUrl;
    }

    public String getActivityUrl() {
        return activityUrl;
    }
}
