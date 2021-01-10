package com.saimon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author Muhammad Saimon
 * @since 27/12/2020 23:27
 */

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/loginerror").setViewName("login-error");
        registry.addViewController("/logoutsuccess").setViewName("logout-success");
    }
}
