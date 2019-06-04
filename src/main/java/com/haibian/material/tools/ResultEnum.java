package com.haibian.material.tools;

/**
 * @author Stopping
 * @data 时间：2017/9/27
 * */
public enum ResultEnum {

    /**访问成功返回*/
    SUCCESS(0,"success"),

    /**数据不存在返回*/
    NOT_FOUND(-1,"notFound [数据不存在 或者 数据为空]"),

    /**异常返回*/
    ERROR(-1,"error [未知异常]"),

    /**参数有异常返回*/
    PARAMETER_ERROR(-1,"parameter error [参数异常:参数为空或者参数类型不符]")
    ;

    private Integer code;

    private String msg;

    private ResultEnum(Integer code,String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
