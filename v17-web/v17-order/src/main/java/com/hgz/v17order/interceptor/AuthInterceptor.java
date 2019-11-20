package com.hgz.v17order.interceptor;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hgz.api.IUserService;
import com.hgz.commons.pojo.ResultBean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author huangguizhao
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Reference
    private IUserService userService;

    /**
     * 前置拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前是否处于登录状态
        //如果是登录状态，则将当前用户信息保存到request中
        //登录了才可以放行
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if("user_token".equals(cookie.getName())){
                    String token = cookie.getValue();
                    ResultBean resultBean = userService.checkIsLogin(token);
                    if("200".equals(resultBean.getStatusCode())){
                        //说明，当前用户已登录
                        request.setAttribute("user",resultBean.getData());
                        return true;
                    }
                }
            }
        }
        //未登录，不放行，跳转到登录页面
        //如果需要实现从哪来回哪去的效果，那么此处需要传递一个url地址
        StringBuffer requestURL = request.getRequestURL();
        response.sendRedirect("http://localhost:9096?referer="+requestURL.toString());
        return false;
    }
}
