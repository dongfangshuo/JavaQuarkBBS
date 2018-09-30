package com.quark.porent.config;

import com.quark.porent.interceptor.LoginInterceptor;
import com.quark.porent.interceptor.WebEnvironmentInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * @Author LHR
 * Create By 2017/8/27
 */
@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Resource
    private LoginInterceptor loginInterceptor;
    @Resource
    private WebEnvironmentInterceptor webEnvironmentInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns(
                "/user/set",
                "/user/seticon",
                "/user/setpsw",
                "/user/api/message/**",
                "/user/api/info",
                "/user/api/password",
                "/user/api/logout",
                "/user/api/detail",
                "/notification/api",
                "/posts/add",
                "/posts/api/post",
                "/reply/api",
                "/upload/api/image",
                "/upload/api/usericon/**",
                "/chat");
        registry.addWebRequestInterceptor(webEnvironmentInterceptor);
        super.addInterceptors(registry);
    }
}
