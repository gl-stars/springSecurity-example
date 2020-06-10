package com.sse.oauth2.db.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @version : 1.0.0
 * @description: Mybatis-plus配置类
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 07日 12:03
 **/
@Configuration
@EnableTransactionManagement
public class MybatisPlusAutoConfigure {

    /***
     * 分页插件，自动识别数据库类型
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
