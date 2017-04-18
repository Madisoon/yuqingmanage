package com.syx.yuqingmanage.springredis;

import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.utils.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Msater Zg on 2017/4/18.
 */

@Configuration
public class SpringBeanConfig {
    @Bean
    public NumberInfoPost numberInfoPost() {
        return new NumberInfoPost();
    }

    @Bean
    public JSONResponse jsonResponse() {
        return new JSONResponse();
    }

    @Bean
    public QqMessagePost qqMessagePost() {
        return new QqMessagePost();
    }

    @Bean
    public AppJsonPost appJsonPost() {
        return new AppJsonPost();
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

    @Bean
    public FailData failData() {
        return new FailData();
    }

    @Bean
    public QqAsyncMessagePost qqAsyncMessagePost() {
        return new QqAsyncMessagePost();
    }
}
