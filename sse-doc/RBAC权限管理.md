# RBAC权限管理

# 一、简介

详细介绍参考：[https://shuwoom.com/?p=3041](https://shuwoom.com/?p=3041)

在`RBAC`模型里面，有3个基础组成部分，分别是：用户、角色和权限。

`RBAC`通过定义角色的权限，并对用户授予某个角色从而来控制用户的权限，实现了用户和权限的逻辑分离（区别于`ACL`模型），极大地方便了权限的管理

下面在讲解之前，先介绍一些名词：

- `sys_user`用户表 ：保存用户信息，每个用户都有唯一的`UID`识别，并被授予不同的角色。
-  `sys_role`角色表：保存角色信息，不同角色具有不同的权限。
- ` sys_permission`权限表：保存系统资源信息。如：菜单、按钮 和对应 `URL`。
- `sys_user_role`用户-角色映射：用户和角色之间的映射关系，是 多对多关系 ，角色表与资源表是多对多关系。
- `sys_role_permission`角色-权限映射：角色和权限之间的映射。

![在这里插入图片描述](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9zaHV3b29tLmNvbS93cC1jb250ZW50L3VwbG9hZHMvMjAxOS8wNC9yYmFjcm9sZS1iYXNlZC1hY2Nlc3MtY29udHJvbC0xLnBuZw?x-oss-process=image/format,png)

# 二、数据脚本和MyBatis-Plus分页

引入`Mybatis-Plus`就不用介绍了，之前我们就引进来了。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200607121238370.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## 2.1、数据表脚本

```mysql
drop table if exists `sys_permission`;
create table `sys_permission` (
  `id` bigint(20) not null auto_increment comment '权限 id',
  `parent_id` bigint(20) default null comment '父权限 id (0为顶级菜单)',
  `name` varchar(64) not null comment '权限名称',
  `code` varchar(64) default null comment '授权标识符',
  `url` varchar(255) default null comment '授权路径',
  `type` int(2) not null default '1' comment '类型(1菜单，2按钮)',
  `icon` varchar(200) default null comment '图标',
  `remark` varchar(200) default null comment '备注',
  `create_date` timestamp not null default current_timestamp,
  `update_date` timestamp not null default current_timestamp,
  primary key (`id`)
) engine=innodb auto_increment=33 default charset=utf8 comment='权限表';

drop table if exists `sys_role`;
create table `sys_role` (
  `id` bigint(20) not null auto_increment comment '角色 id',
  `name` varchar(64) not null comment '角色名称',
  `remark` varchar(200) default null comment '角色说明',
  `create_date` timestamp not null default current_timestamp,
  `update_date` timestamp not null default current_timestamp,
  primary key (`id`)
) engine=innodb auto_increment=12 default charset=utf8 comment='角色表';

drop table if exists `sys_role_permission`;
create table `sys_role_permission` (
  `id` bigint(20) not null auto_increment comment '主键 id',
  `role_id` bigint(20) not null comment '角色 id',
  `permission_id` bigint(20) not null comment '权限 id',
  primary key (`id`)
) engine=innodb auto_increment=26 default charset=utf8 comment='角色权限表';

drop table if exists `sys_user`;
create table `sys_user` (
  `id` bigint(20) not null auto_increment comment '用户 id',
  `username` varchar(50) not null comment '用户名',
  `password` varchar(64) not null comment '密码，加密存储, admin/1234',
  `is_account_non_expired` int(2) default '1' comment '帐户是否过期(1 未过期，0已过期)',
  `is_account_non_locked` int(2) default '1' comment '帐户是否被锁定(1 未过期，0已过期)',
  `is_credentials_non_expired` int(2) default '1' comment '密码是否过期(1 未过期，0已过期)',
  `is_enabled` int(2) default '1' comment '帐户是否可用(1 可用，0 删除用户)',
  `nick_name` varchar(64) default null comment '昵称',
  `mobile` varchar(20) default null comment '注册手机号',
  `email` varchar(50) default null comment '注册邮箱',
  `create_date` timestamp not null default current_timestamp,
  `update_date` timestamp not null default current_timestamp,
  primary key (`id`),
  unique key `username` (`username`) using btree,
  unique key `mobile` (`mobile`) using btree,
  unique key `email` (`email`) using btree
) engine=innodb auto_increment=11 default charset=utf8 comment='用户表';

drop table if exists `sys_user_role`;
create table `sys_user_role` (
  `id` bigint(20) not null auto_increment comment '主键 id',
  `user_id` bigint(20) not null comment '用户 id',
  `role_id` bigint(20) not null comment '角色 id',
  primary key (`id`)
) engine=innodb auto_increment=3 default charset=utf8 comment='用户角色表';
```

在`sse-doc/sql文件RBAC脚本.sql`有插入数据，可以做测试时使用。



## 2.2、配置分页效果

创建 `com.sse.oauth2.db.config.MybatisPlusAutoConfigure`类配置`MP（Mybatis-Plus）`相关信息，在类上添加 `@Configuration`注解标识为配置类，`@EnableTransactionManagement`开启事务支持。实例化`PaginationInterceptor`类，并注入容器，这就分页才会有效。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200607121102508.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)



## 2.3、创建用户管理实体类

这个类只是实体类有些不一样，其他的与平时的业务逻辑一样。

创建用户管理实体类 `com.sse.oauth2.model.SysUser`，并实现`UserDetails`认证用户信息封装接口，其实`UserDetails`接口有一个实现类`User`，之前也在使用，但是有些功能我们要扩展不太方便，所以就直接定义`SysUser`提示类代替。`UserDetails`接口定义如下：

```java
public interface UserDetails extends Serializable {
    
    // 此用户可访问的资源权限
    Collection<? extends GrantedAuthority> getAuthorities();

    // 密码
    String getPassword();

    // 用户名
    String getUsername();

    // 帐户是否过期(true 未过期，false 已过期)
    boolean isAccountNonExpired();

    // 帐户是否被锁定（true 未锁定，false 已锁定），锁定的用户是可以恢复的
    boolean isAccountNonLocked();

    // 密码是否过期（安全级别比较高的系统，如30天要求更改密码，true 未过期，false 过期）
    boolean isCredentialsNonExpired();

    // 帐户是否可用（一般指定是否删除，系统一般不会真正的删除用户信息，而是假删除，通过一个状态码标志
	// 用户被删除）删除的用户是可以恢复的
    boolean isEnabled();
}
```

### 编写实体类SysUser

```java
package com.sse.oauth2.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @version : 1.0.0
 * @description: 用户管理实体类
 * @author: GL
 * @program: springSecurity-example
 * @create: 2020年 06月 07日 16:45
 **/
@Data
public class SysUser implements UserDetails {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码，加密存储
     */
    private String password;
    /**
     * 帐户是否过期(true(1) 未过期，false(0)已过期)
     * 设置默认值为true，新增用户默认未过期
     *
     * 注意：生成的setter和getter方法没有 `is`
     * setAccountNonExpired
     * getAccountNonExpired
     * 所以前端获取时也不要有 `is`
     */
    private boolean isAccountNonExpired = true;
    /**
     * 帐户是否被锁定(true(1) 未过期，false(0)已过期)
     * 设置默认值为true，新增用户默认未过期
     */
    private boolean isAccountNonLocked = true;
    /**
     * 密码是否过期(true(1) 未过期，false(0)已过期)
     * 设置默认值为true，新增用户默认未过期
     */
    private boolean isCredentialsNonExpired = true;
    /**
     * 帐户是否可用(true(1) 可用，false(0)未删除)
     * 设置默认值为true，新增用户默认未过期
     */
    private boolean isEnabled = true;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    private Date createDate;
    private Date updateDate;
    /**
     * 拥有权限集合
     * @TableField(exist = false) 该属性不是数据库表字段
     */
    @TableField(exist = false)
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 父接口认证方法 start
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }
    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }
    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * 拥有角色集合
     */
    @TableField(exist = false)
    private List<SysRole> roleList = new ArrayList<SysRole>();

    /**
     * 获取所有角色id
     */
    @TableField(exist = false)
    private List<Long> roleIds = new ArrayList<Long>();
    public List<Long> getRoleIds() {
        if(CollectionUtils.isNotEmpty(roleList)) {
            roleIds = new ArrayList<Long>();
            for(SysRole role : roleList) {
                roleIds.add(role.getId());
            }
        }
        return roleIds;
    }
    @TableField(exist = false)
    private List<SysPermission> permissions = new ArrayList<SysPermission>();
}
```

### 定义mapper接口

定义`SysUserMapper`并<font style="font-weight: bold;font-size:15px" color="red">继承</font>`BaseMapper`接口。

```java
/**
 * 实现MyBatis-Plus封装的 BaseMapper<T> 接口,它有很多对 T 表的数据操作方法
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 07日 16:53
 **/
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
```

### 创建Service类

```java
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
}

```

创建实现类

```java
package com.sse.oauth2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sse.oauth2.mapper.SysUserMapper;
import com.sse.oauth2.model.SysUser;
import com.sse.oauth2.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 07日 16:57
 **/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser findByUsername(String username) {
        if(StringUtils.isEmpty(username)) {
            return null;
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        // baseMapper 对应的是就是 SysUserMapper
        return baseMapper.selectOne(queryWrapper);
    }
}
```

### 测试

创建测试类`TestWebApplication`

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020061011394625.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

这样就可以将用户信息查询出来了。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200610114230210.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## 2.4、创建角色管理 SysRole

### 编写实体类SysRole

创建`SysRole`类，并实现序列化接口`Serializable`，其实上面的用户管理没有手动实现序列化接口，因为实现了`UserDetails`接口，而这个接口实现序列化了。

```java
package com.sse.oauth2.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 07日 21:17
 **/
@Data
public class SysRole implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色描述
     */
    private String remark;

    private Date createDate;
    private Date updateDate;

    /**
     * 存储当前角色的权限资源对象集合
     * 修改角色时用到
     */
    @TableField(exist = false)
    private List<SysPermission> perList = new ArrayList<SysPermission>();
    /**
     * 存储当前角色的权限资源ID集合
     * 修改角色时用到
     */
    @TableField(exist = false)
    private List<Long> perIds = new ArrayList<Long>();

    public List<Long> getPerIds() {
        if(CollectionUtils.isNotEmpty(perList)) {
            perIds =new ArrayList<Long>();
            for(SysPermission per : perList) {
                perIds.add(per.getId());
            }
        }
        return perIds;
    }
}
```

### 编写Mapper接口

编写`SysRoleMapper`接口并继承`BaseMapper`接口。

```java
package com.sse.oauth2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sse.oauth2.model.SysRole;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 10日 11:47
 **/
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
```

### 编写Service类

```java
package com.sse.oauth2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sse.oauth2.model.SysRole;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 10日 11:49
 **/
public interface SysRoleService extends IService<SysRole> {
}
```

编写`Service`实现类

```java
package com.sse.oauth2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sse.oauth2.mapper.SysRoleMapper;
import com.sse.oauth2.model.SysRole;
import com.sse.oauth2.service.SysRoleService;
import org.springframework.stereotype.Service;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 10日 11:51
 **/
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

}
```

### 编写测试类

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200610115555941.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

测试结果：

这样就可以将角色的所欲信息查询出来了，但是并没有做关系分配问题，所以下面你会发现`perList`并没有数据。

```
sysRole:SysRole(id=9, name=超级管理员, remark=拥有所有的权限, createDate=Tue Aug 08 11:11:11 CST 2023, updateDate=Tue Aug 08 11:11:11 CST 2023, perList=[], perIds=[])
```

## 2.5、权限管理 SysPermission

### 编写实体类SysPermission

```java
package com.sse.oauth2.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 07日 21:17
 **/
@Data
public class SysPermission implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父资源id,给它初始值 0
     * 新增和修改页面上默认的父资源id
     */
    private Long parentId = 0L ;
    /**
     * 用于新增和修改页面上默认的根菜单名称
     */
    @TableField(exist = false)
    private String parentName = "根菜单";

    /**
     * 名称
     */
    private String name;

    private String code;

    /**
     * 访问地址
     */
    private String url;

    /**
     * 菜单：1，按钮：2
     */
    private Integer type;

    /**
     * 图标
     */
    private String icon;

    /**
     * 描述
     */
    private String remark;
    private Date createDate;
    private Date updateDate;


    /**
     * 所有子权限对象集合
     * 左侧菜单渲染时要用
     */
    @TableField(exist = false)
    private List<SysPermission> children;

    /**
     * 所有子权限 URL 集合
     * 左侧菜单渲染时要用
     */
    @TableField(exist = false)
    private List<String> childrenUrl;
}
```

### 创建Mapper接口

创建`SysPermissionMapper`继承 `BaseMapper` 接口定义方法： `fifindByUserId` ，通过用户`ID`查询该用户拥有的权限资源, 需要我们自已写`sql`查询。

```java
package com.sse.oauth2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sse.oauth2.model.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 10日 12:03
 **/
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    
    List<SysPermission> selectPermissionByUserId(@Param("userId") Long userId);

}
```

创建`SysPermissionMapper.xml`

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200610121800922.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

```xml
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.sse.oauth2.mapper.SysPermissionMapper">

    <select id="selectPermissionByUserId" resultType="com.sse.oauth2.model.SysPermission">
      SELECT DISTINCT
            p.id,
            p.parent_id,
            p. NAME,
            p. CODE,
            p.url,
            p.type,
            p.icon,
            p.remark,
            p.create_date,
            p.update_date
        FROM
            sys_user AS u
        LEFT JOIN sys_user_role AS ur ON u.id = ur.user_id
        LEFT JOIN sys_role AS r ON ur.role_id = r.id
        LEFT JOIN sys_role_permission AS rp ON rp.role_id = r.id
        LEFT JOIN sys_permission AS p ON rp.permission_id = p.id
        WHERE
            u.id = #{userId}
    </select>

</mapper>
```

### 编写Service类

```java
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
```

编写实现类

```java
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
```

### 编写测试类

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200610122004625.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

当前版本号：`38a41a72a133e891e430e7b4977de6b30fa3b80f`