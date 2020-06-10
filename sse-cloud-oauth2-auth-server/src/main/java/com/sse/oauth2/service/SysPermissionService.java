package com.sse.oauth2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sse.oauth2.model.SysPermission;

import java.util.List;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 10日 12:05
 **/
public interface SysPermissionService  extends IService<SysPermission> {

    /**
     * 通过用户id查询所拥有权限
     * @param userId
     * @return
     */
    List<SysPermission> findByUserId(Long userId);

}