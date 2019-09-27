package com.mmall.common;

import com.mmall.exception.ParamException;
import com.mmall.exception.PermissionExcrption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) {
       String url = request.getRequestURI().toString();
        ModelAndView mv;
        String defaultMsg = "System error";

        // json, .page
        //这里我们要求项目中所有请求json数据，都使用。json结尾
        if(url.endsWith(".json")){
            if(ex instanceof PermissionExcrption || ex instanceof ParamException){
                JsonData result = JsonData.fail(ex.getMessage());
                mv = new ModelAndView("jsonView",result.toMap());
            }else{
                log.error("unknow json exception ,url :"+ url,ex);
                JsonData result = JsonData.fail(defaultMsg);
                mv = new ModelAndView("jsonView",result.toMap());
            }
        }else if( url.endsWith(".page")){ //这里我们要求项目中所有请求page数据，都使用.page结尾
            log.error("unknow page exception ,url :"+ url,ex);
            JsonData result = JsonData.fail(defaultMsg);
            mv = new ModelAndView("exception",result.toMap());
        }else{
            log.error("unknow exception ,url :"+ url,ex);
            JsonData result = JsonData.fail(defaultMsg);
            mv = new ModelAndView("jsonView",result.toMap());
        }
        return mv;
    }
}
