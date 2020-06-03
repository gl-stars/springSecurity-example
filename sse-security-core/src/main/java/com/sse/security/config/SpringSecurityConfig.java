package com.sse.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @version : 1.0.0
 * @description: 安全配置类
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 03日 13:43
 **/
@Slf4j
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder ;

    /**
     * 认证管理器：
     *  1. 认证信息（用户名，密码）
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 数据库存储的密码必须是加密后的，不然会报错：There is no PasswordEncoder mapped for the id "null"
        String password = passwordEncoder.encode("1234");
        log.info("加密之后存储的密码：" + password);
        auth.inMemoryAuthentication().withUser("admin")
                .password(password).authorities("ADMIN");
    }

    /**
     * 资源权限配置：
     * 1. 被拦截的资源
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.httpBasic() // 采用 httpBasic认证方式
        http.formLogin() // 表单登录方式
                .and()
                .authorizeRequests() // 认证请求
                .anyRequest().authenticated() //所有访问该应用的http请求都要通过身份认证才可以访问
        ; // 注意不要少了分号
    }
}
