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
