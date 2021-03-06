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
