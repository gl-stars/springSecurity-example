package com.sse.oauth2.server.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;

/**
 * Token管理工具
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 06日 15:01
 **/
@Configuration
public class TokenConfig {

    /**
     * Redis 管理令牌
     * 添加redis 依赖后, 容器就会有 RedisConnectionFactory 实例
     */
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * jdbc管理token
     * @return
     */
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource dataSource(){
        return new DruidDataSource();
    }

    /**
     * 访问令牌的转换器
     * @return
     */
   @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // 对称加密进行签名令牌，资源服务器也要采用此密钥来进行解密,来校验令牌合法性
        converter.setSigningKey("abcdefg");
        // 采用非对称加密jwt

        // 第1个参数就是密钥证书文件，第2个参数 密钥口令(-keypass), 私钥进行签名
//        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(
//                new ClassPathResource("oauth2.jks"), "oauth2".toCharArray());
        // 指定非对称加密 oauth2 别名
//        converter.setKeyPair(factory.getKeyPair("oauth2"));
        return converter;
    }

    /**
     * 指定令牌管理方式
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        // redis 管理令牌
//        return new RedisTokenStore(redisConnectionFactory);
        // 注入数据源
//        return new JdbcTokenStore(dataSource());
        // 使用JWT管理令牌
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

}
