package com.sse.oauth2.server.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 06日 18:26
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAuthApplication {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 测试加密结果
     */
    @Test
    public void testPwd() {
        // 指定数据获取加密后的结果
        System.out.println(passwordEncoder.encode("123456"));
    }
}
