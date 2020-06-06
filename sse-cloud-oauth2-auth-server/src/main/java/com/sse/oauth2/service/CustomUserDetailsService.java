package com.sse.oauth2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 06日 13:17
 **/
@Component("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder ;

    /**
     * 设置用户名和权限，后期会使用数据库表的形式实现
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 设置用户名和权限
        return new User("admin", passwordEncoder.encode("1234"),
                AuthorityUtils.commaSeparatedStringToAuthorityList("product"));
    }
}
