package com.smoothstack.investment_orchestrator.config;

import com.smoothstack.investment_orchestrator.logging.FileLogger;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @PostConstruct
    public void init() {
        FileLogger.createLogDirectory();
    }
}
