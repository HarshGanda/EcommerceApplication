package com.ecommerce.catalog.config;
}
    }
        return new CorsFilter(source);
        source.registerCorsConfiguration("/**", config);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);
        CorsConfiguration config = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    public CorsFilter corsFilter() {
    @Bean

public class CorsConfig {
@Configuration

import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;


