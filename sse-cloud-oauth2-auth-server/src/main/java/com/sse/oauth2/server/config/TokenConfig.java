package com.sse.oauth2.server.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

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

    @Bean
    public TokenStore tokenStore() {
        // redis 管理令牌
        return new RedisTokenStore(redisConnectionFactory);
//        return new JdbcTokenStore(dataSource());
    }

}
