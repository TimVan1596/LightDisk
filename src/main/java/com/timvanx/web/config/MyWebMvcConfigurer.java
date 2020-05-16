package com.timvanx.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <h3>BlockChain</h3>
 * <p></p>
 *
 * @author : TimVan
 * @date : 2020-05-16 21:37
 **/
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/index.html", "/registry","/page/login-1.html","/page/newwallet.html")
                .excludePathPatterns("/assets/**");

    }

}
