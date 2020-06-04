package com.sse.web.controller;

import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    /**
     * 创建一个登录地址，否则跳转到登录路径是会报404，找不到
     * @return
     */
    @RequestMapping("/login/page")
    public String getLogin(){
        return "这是一个登录页面" ;
    }

    /**
     * 获取验证码
     * @param deviceId
     * @param response
     */
    @GetMapping("/validata/code/{deviceId}")
    public void createCode(@PathVariable String deviceId, HttpServletResponse response, HttpServletRequest request) throws IOException {
        // 三个参数分别为宽、高、位数
        GifCaptcha gifCaptcha = new GifCaptcha(100, 35, 4);
        // 设置类型：字母数字混合
        gifCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        // 保存验证码
        request.getSession().setAttribute(deviceId , gifCaptcha.text().toLowerCase());
        // 输出图片流
        gifCaptcha.out(response.getOutputStream());
    }
}
