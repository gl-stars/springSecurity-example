package com.sse.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * Token管理工具
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 06日 15:01
 **/
@Configuration
public class TokenConfig {

    /**
     * 访问令牌的转换器
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // 对称加密进行签名令牌，资源服务器也要采用此密钥来进行解密,来校验令牌合法性
        converter.setSigningKey("abcdefg");
        // 非对称加密，资源服务器使用公钥解密 public.txt
//        ClassPathResource resource = new ClassPathResource("public.txt");
//        String publicKey = null;
//        try {
//            publicKey = IOUtils.toString(resource.getInputStream(), "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        converter.setVerifierKey(publicKey);
        return converter;
    }

    /**
     * 指定token管理方式
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        // 指定jwt管理令牌
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

}
