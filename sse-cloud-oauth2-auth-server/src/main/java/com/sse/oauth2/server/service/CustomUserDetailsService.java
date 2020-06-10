package com.sse.oauth2.server.service;

import com.sse.oauth2.model.SysUser;
import com.sse.oauth2.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 10日 18:07
 **/
@Component("customUserDetailsService")
public class CustomUserDetailsService extends AbstractUserDetailsService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SysUserService sysUserService;

    @Override
    SysUser findSysUser(String usernameOrMobile){
        logger.info("请求认证的用户名：" + usernameOrMobile);
        return sysUserService.findByUsername(usernameOrMobile);
    }

}
