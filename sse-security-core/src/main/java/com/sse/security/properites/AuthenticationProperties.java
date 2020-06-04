package com.sse.security.properites;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 04日 16:45
 **/
@Getter
@Setter
public class AuthenticationProperties {
    private String[] loginPage = {"/login/page"};
    private String loginProcessingUrl = "/login/form";
    private String usernameParameter = "name";
    private String passwordParameter = "pwd";
    private String[] staticPaths = {"/dist/**", "/modules/**", "/plugins/**"};

    /**
     * 配置认证相应方式
     */
    private LoginResponseType loginType = LoginResponseType.JSON;
}
