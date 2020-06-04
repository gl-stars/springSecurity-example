package com.sse.security.filter;

import com.sse.security.exception.ValidateCodeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码校验过滤器
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 04日 19:11
 **/
@Slf4j
@Component("validateCodeFilter")
public class ValidateCodeFilter extends OncePerRequestFilter {

    /**
     * 验证请求url与配置的url是否匹配的工具类
     */
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 验证码校验失败处理器
     */
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 返回true代表不执行过滤器，false代表执行
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        /**
         * 一般在登录提交的时候验证验证码，那么这里的路径就是登陆的陆经理咯
         */
        if (pathMatcher.match("/index", request.getRequestURI())) {
            // 执行过滤器
            return false;
        }
        return true;
    }

    /**
     * 过滤器，校验验证码是否正确
     *
     * @param request
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            validate(request);
        } catch (ValidateCodeException e) {
            authenticationFailureHandler.onAuthenticationFailure(request, response, e);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * 校验验证码
     * @param request
     */
    public void validate(HttpServletRequest request) {
        String deviceId = request.getParameter("deviceId");
        if (StringUtils.isBlank(deviceId)) {
            throw new ValidateCodeException("请在请求参数中携带deviceId参数");
        }

        // 获取Session中的验证码
        String code = (String) request.getSession().getAttribute(deviceId);
        String codeInRequest;
        try {
            codeInRequest = ServletRequestUtils.getStringParameter(request, "validCode");
        } catch (ServletRequestBindingException e) {
            throw new ValidateCodeException("获取验证码的值失败");
        }
        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("请填写验证码");
        }

        if (code == null) {
            throw new ValidateCodeException("验证码不存在或已过期");
        }

        if (!StringUtils.equals(code, codeInRequest.toLowerCase())) {
            throw new ValidateCodeException("验证码不正确");
        }
        // 删除session中的验证码
       request.getSession().removeAttribute(deviceId);
    }
}
