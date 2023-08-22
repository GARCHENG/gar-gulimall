package com.garcheng.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GuliCorsConfigration {

    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfigration = new CorsConfiguration();
        corsConfigration.addAllowedHeader("*");
        corsConfigration.addAllowedMethod("*");
        corsConfigration.addAllowedOrigin("*");
        corsConfigration.setAllowCredentials(true);

        source.registerCorsConfiguration("/**",corsConfigration);
        return new CorsWebFilter(source);
    }
}
