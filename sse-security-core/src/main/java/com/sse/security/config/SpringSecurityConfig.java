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
 * 安全控制中心
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 03日 13:43
 **/
@Slf4j
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 密码加密
     */
    @Autowired
    private PasswordEncoder passwordEncoder ;

    /**
     * 认证管理器：
     * 1、认证信息提供方式（用户名、密码、当前用户的资源权限）
     * 2、可采用内存存储方式，也可能采用数据库方式等
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 数据库存储的密码必须是加密后的，不然会报错：There is no PasswordEncoder mapped for the id "null"
        String password = passwordEncoder.encode("1234");
        log.info("加密之后存储的密码：" + password);
        // 设置用户名和角色
        auth.inMemoryAuthentication().withUser("admin")
                .password(password).authorities("ADMIN");
    }

    /**
     * 资源权限配置（过滤器链）:
     * 1、被拦截的资源
     * 2、资源所对应的角色权限
     * 3、定义认证方式：httpBasic 、httpForm
     * 4、定制登录页面、登录请求地址、错误处理方式
     * 5、自定义 spring security 过滤器
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 采用 httpBasic认证方式
        //        http.httpBasic()
        // 表单登录方式
        http.formLogin()
                .and()
                // 认证请求
                .authorizeRequests()
                .anyRequest()
                //所有访问该应用的http请求都要通过身份认证才可以访问
                .authenticated()
        ; // 注意不要少了分号
    }
}
