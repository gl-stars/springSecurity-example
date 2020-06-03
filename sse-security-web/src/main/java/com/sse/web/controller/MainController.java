package com.sse.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version : 1.0.0
 * @description: 前端控制器
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 03日 14:51
 **/
@RestController
public class MainController {

    @GetMapping("/index")
    public String autoIndex(){
        return "认证通过，可以进行操作了。";
    }
}
