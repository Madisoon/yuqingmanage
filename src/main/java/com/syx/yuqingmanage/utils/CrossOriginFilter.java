package com.syx.yuqingmanage.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Msater Zg on 2017/3/16.
 */
public class CrossOriginFilter implements Filter {

    private FilterConfig config = null;

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }

    @Override
    public void destroy() {
        this.config = null;
    }

    /**
     * @author wwhhf
     * @comment 跨域的设置
     * @since 2016/5/30
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 表明它允许"http://xxx"发起跨域请求
        httpResponse.addHeader("Access-Control-Allow-Origin",
                "*");
        // 表明在xxx秒内，不需要再发送预检验请求，可以缓存该结果
        httpResponse.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");
        // 表明它允许xxx的外域请求
        httpResponse.setHeader("Access-Control-Max-Age",
                "1800");
        // 表明它允许跨域请求包含xxx头
        httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, client_id, uuid, Authorization");
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // 支持HTTP 1.1.
        httpResponse.setHeader("Pragma", "no-cache");
        /*httpResponse.addHeader("Access-Control-Allow-Headers", "apiToken");*/
        chain.doFilter(request, response);
    }

}