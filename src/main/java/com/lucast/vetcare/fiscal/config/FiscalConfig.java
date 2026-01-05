package com.lucast.vetcare.fiscal.config;

import com.lucast.vetcare.fiscal.FiscalProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FiscalProperties.class)
public class FiscalConfig {
}
