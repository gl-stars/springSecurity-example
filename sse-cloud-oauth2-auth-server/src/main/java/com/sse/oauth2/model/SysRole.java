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
