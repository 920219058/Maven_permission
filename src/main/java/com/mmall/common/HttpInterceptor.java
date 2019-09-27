package com.mmall.common;

import com.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class HttpInterceptor extends HandlerInterceptorAdapter {

    private static final String START_TIME = "requestStartTime";
    @Override // 请求准备实现的时候，之前执行
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // return super.preHandle(request, response, handler);
        String url = request.getRequestURI().toString();
        Map parameterMap = request.getParameterMap();
        Long statTime = System.currentTimeMillis();
        request.setAttribute(START_TIME,statTime);
        log.info("request start. url:{},params:{}",url, JsonMapper.obj2String(parameterMap));
        return true;
    }

    @Override // 正常请求结束时候执行
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String url = request.getRequestURI().toString();
        Map parameterMap = request.getParameterMap();
        long start = (long)request.getAttribute(START_TIME);
        long end = System.currentTimeMillis();
        removeThreadLocalInfo();
        log.info("request complete. url:{},params:{}. cost:{}",url, JsonMapper.obj2String(parameterMap),end -start);
    }

    @Override  // 任何时候请求结束都会执行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url = request.getRequestURI().toString();
        Map parameterMap = request.getParameterMap();
        long start = (long)request.getAttribute(START_TIME);
        long end = System.currentTimeMillis();
        removeThreadLocalInfo();
        log.info("request finished. url:{},params:{}. cost:{}",url, JsonMapper.obj2String(parameterMap),end -start);
    }

    public void removeThreadLocalInfo(){
        RequestHolder.remove();
    }
}
