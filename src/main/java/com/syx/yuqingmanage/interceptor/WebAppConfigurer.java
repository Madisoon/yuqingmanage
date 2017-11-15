package com.syx.yuqingmanage.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Msater Zg on 2017/4/5.
 * 拦截器管理工具
 */
@Configuration
public class WebAppConfigurer extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链 ,可以添加多个
        // addPathPatterns 用于添加拦截规则 , excludePathPatterns 用户排除拦截

        // 拦截除
        registry.addInterceptor(new ApiInterceptor()).addPathPatterns("/**");
       /* registry.addInterceptor(new ExcludeInterceptor()).excludePathPatterns("/system/judgeUser");  //对来自/user*//** 这个链接来的请求进行拦截*/
        super.addInterceptors(registry);
    }
}
