package com.quark.porent.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by gaopengju on 2018/9/16.
 */
@Service
public class WebEnvironmentInterceptor implements WebRequestInterceptor {
    @Override
    public void preHandle(WebRequest request) throws Exception {

    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        DispatcherServletWebRequest dispatcherServletWebRequest = (DispatcherServletWebRequest) request;
        HttpServletRequest httpServletRequest = dispatcherServletWebRequest.getNativeRequest(HttpServletRequest.class);
        StringBuffer sb = httpServletRequest.getRequestURL();
        if(model != null){
            model.put("apiUrl",sb.substring(0,sb.indexOf(httpServletRequest.getRequestURI())));
        }
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {

    }
}
