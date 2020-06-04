package com.sse.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码异常处理类
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 04日 19:28
 **/
public class ValidateCodeException extends AuthenticationException {
    private static final long serialVersionUID = -7285211528095468156L;

    public ValidateCodeException(String msg) {
        super(msg);
    }
}
