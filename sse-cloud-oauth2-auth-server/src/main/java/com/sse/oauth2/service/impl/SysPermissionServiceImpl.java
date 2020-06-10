package com.sse.oauth2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sse.oauth2.mapper.SysPermissionMapper;
import com.sse.oauth2.model.SysPermission;
import com.sse.oauth2.service.SysPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 10日 12:06
 **/
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {


    @Override
    public List<SysPermission> findByUserId(Long userId) {
        if(userId == null) {
            return null;
        }
        List<SysPermission> permissionList = baseMapper.selectPermissionByUserId(userId);
        // 如果没有权限，则将集合中的数据null移除
//        permissionList.remove(null);
        return permissionList;
    }

}