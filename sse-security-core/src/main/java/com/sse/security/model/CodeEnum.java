package com.sse.security.model;

/**
 * @version : 1.0.0
 * @description: 后端数据状态枚举
 * @author: GL
 * @program: mind-center-platform
 * @create: 2020年 05月 17日 16:51
 **/
public enum CodeEnum {
    /**
     * 成功
     */
    SUCCESS(0),
    /**
     * 失败
     */
    ERROR(1);

    private Integer code;
    CodeEnum(Integer code){
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
