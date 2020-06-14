package com.sse.oauth2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源服务器相关配置类
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 10日 21:58
 **/
@RestController
@RequestMapping("/product")
public class ProductController {

    /**
     * 访问访问权限设置，必须有product权限的才可以访问
     * @PreAuthorize("hasAuthority('product')")
     * @return
     */
    @GetMapping("/list")
//    @PreAuthorize("hasAuthority('sys:user:list')")
    public List<String> list() {
        List<String> list = new ArrayList<>();
        list.add("权限框架学习");
        list.add("高级java学习");
        list.add("数据中台学习");
        list.add("领域驱动设计思想");
        return list;
    }
}