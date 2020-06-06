package com.sse.security.redis;

/**
 * @version : 1.0.0
 * @description:
 * @author: GL
 * @program: mind-center-platform
 * @create: 2020年 05月 17日 16:10
 **/
public interface RedisSubscribeCallback {
    void callback(String msg);
}
