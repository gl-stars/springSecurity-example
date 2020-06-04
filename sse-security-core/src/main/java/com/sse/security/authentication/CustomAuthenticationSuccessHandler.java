package com.sse.security.authentication;

import com.sse.security.model.Result;
import com.sse.security.properites.LoginResponseType;
import com.sse.security.properites.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证成功处理类
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 04日 17:17
 **/
@Component("customAuthenticationSuccessHandler")
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private SecurityProperties properties ;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 判断是重定向还是返回上一次请求的地址
        if (LoginResponseType.JSON.equals(properties.getAuthentication().getLoginType())) {
            // 认证成功后，响应JSON字符串
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(Result.succeed("认证成功").toString());
        }else {
            //重定向到上次请求的地址上，引发跳转到认证页面的地址
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
