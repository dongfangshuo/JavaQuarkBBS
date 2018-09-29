package com.quark.porent.interceptor;

import com.alibaba.fastjson.JSON;
import com.quark.common.dto.QuarkResult;
import com.quark.common.entity.User;
import com.quark.porent.service.UserService;
import com.quark.porent.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author LHR
 * Create By 2017/8/27
 *
 * 登录拦截
 */
@Service
public class LoginInterceptor implements HandlerInterceptor {

    public static final String TOKEN = "QUARK_TOKEN";
    @Autowired
    private UserService userService;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        String token = CookieUtils.getCookieValue(httpServletRequest, TOKEN);
        String requestUri = httpServletRequest.getRequestURI();
        Boolean isapi = requestUri.matches(".*/api/?.*");
        if (token==null) {
            resonse(isapi,httpServletResponse);
            // 返回false
            return false;
        }
        User user = userService.getUserByToken(token);
        SubjectHolder.put(user);
        // 取不到用户信息
        if (user == null) {
            resonse(isapi,httpServletResponse);
            // 返回false
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }


    private void resonse(Boolean isApi, HttpServletResponse httpServletResponse){
        if(isApi){
            try {
                PrintWriter printWriter = httpServletResponse.getWriter();
                printWriter.print(JSON.toJSONString(QuarkResult.error("请登录")));
                printWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                httpServletResponse.sendRedirect("/user/login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
