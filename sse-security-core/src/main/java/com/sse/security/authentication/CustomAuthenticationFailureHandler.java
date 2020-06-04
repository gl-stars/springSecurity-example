package com.sse.security.authentication;

import com.sse.security.model.Result;
import com.sse.security.properites.LoginResponseType;
import com.sse.security.properites.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败处理类
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 04日 17:25
 **/
@Component("customAuthenticationFailureHandler")
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private SecurityProperties properties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        /**
         * 判断相应JSON格式还是重定向地址
         */
        if (LoginResponseType.JSON.equals(properties.getAuthentication().getLoginType())) {
            // 认证失败响应JSON字符串，
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(Result.failed("认证失败").toString());
        }else {
            // 重写向回认证页面，注意加上 ?error
            super.setDefaultFailureUrl(
                    properties.getAuthentication().getLoginPage()+"?error");
            super.onAuthenticationFailure(request, response, exception);
        }
    }

}
