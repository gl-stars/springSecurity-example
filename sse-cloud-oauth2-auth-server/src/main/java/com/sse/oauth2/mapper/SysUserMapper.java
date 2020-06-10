package com.sse.oauth2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sse.oauth2.model.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 实现MyBatis-Plus封装的 BaseMapper<T> 接口,它有很多对 T 表的数据操作方法
 * @version : 1.0.0
 * @author: GL
 * @create: 2020年 06月 07日 16:53
 **/
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
