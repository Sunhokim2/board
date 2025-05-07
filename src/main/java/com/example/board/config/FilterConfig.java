package com.example.board.config;

import com.example.board.filter.LoginCheckFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<Filter> filterBean() {
        FilterRegistrationBean<Filter> bean =
                new FilterRegistrationBean<>(new LoginCheckFilter());
        bean.addUrlPatterns("/signin");
        return bean;
    }
}
