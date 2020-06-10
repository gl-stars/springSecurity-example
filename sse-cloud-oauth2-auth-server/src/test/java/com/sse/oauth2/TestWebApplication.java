package com.sse.oauth2;

import com.sse.oauth2.model.SysPermission;
import com.sse.oauth2.model.SysRole;
import com.sse.oauth2.model.SysUser;
import com.sse.oauth2.service.SysPermissionService;
import com.sse.oauth2.service.SysRoleService;
import com.sse.oauth2.service.SysUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 07日 21:32
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestWebApplication {

    /**
     * 用户
     */
    @Autowired
    SysUserService sysUserService;

    /**
     * 角色
     */
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 权限管理
     */
    @Autowired
    SysPermissionService sysPermissionService;

    @Test
    public void testSysUser() {
        List<SysUser> list = sysUserService.list();
        System.out.println("list:" + list);

//        SysUser user = sysUserService.findByUsername("admin");
//        System.out.println("user: " + user);
    }

    @Test
    public void testSysRole(){
        // 通过角色id查询角色信息
        SysRole sysRole = sysRoleService.getById(9);
        System.out.println("sysRole:" + sysRole);
    }

    @Test
    public void testSysPer(){
        SysPermission permission = sysPermissionService.getById(18);
        System.out.println(permission);
        List<SysPermission> permissions = sysPermissionService.findByUserId(9L);
        System.out.println("permissions:" + permissions.size());
    }
}
