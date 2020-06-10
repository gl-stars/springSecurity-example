package com.sse.oauth2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sse.oauth2.model.SysUser;

/**
 *  实现 IService<T> 接口，提供了常用更复杂的对 T 数据表的操作，
 *  比如：支持 Lambda 表达式，批量删除、自动新增或更新操作
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 07日 16:55
 **/
public interface SysUserService extends IService<SysUser> {

    /**
     * 通过用户名查询
     * @param username 用户名
     * @return 用户信息
     */
    SysUser findByUsername(String username);

    /**
     * 通过手机号查询
     * @param mobile 手机号
     * @return 用户信息
     */
    SysUser findByMobile(String mobile);
}
