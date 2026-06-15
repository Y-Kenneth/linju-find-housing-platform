package com.linjufind.config;

import com.linjufind.filter.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilter() {
        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoginFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }
}
