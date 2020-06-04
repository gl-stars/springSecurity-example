package com.sse.security.properites;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 04日 16:46
 **/
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sse.security")
public class SecurityProperties {

    /***
     * 配置的所有信息都映射到这哥类上
     */
    private AuthenticationProperties authentication ;
}
