package com.haibian.material.tools;

/**
 * @author Stopping
 * @Data 2017/9/25
 * @see json返回值统一格式化工具类
 * */
public class ResultUtil {
    /**
     * 数据交互成功返回
     * @param object json返回的数据
     * */
    public static Result success(Object object){
        if(object==null){
            object = "";
        }
        return new Result(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),object);
    }
    /**
     * 数据交互
     * */
    public static Result notFound(){
        return  new Result(ResultEnum.NOT_FOUND.getCode(),ResultEnum.NOT_FOUND.getMsg(),"");
    }
    /**
     * 参数异常
     * */
    public static Result parameterError(){
        return new Result(ResultEnum.PARAMETER_ERROR.getCode(),ResultEnum.PARAMETER_ERROR.getMsg(),"");
    }
    /**
     * 系统异常
     * */
    public static Result systemError(){
        return new Result(ResultEnum.ERROR.getCode(),ResultEnum.ERROR.getMsg(),"");
    }

}
