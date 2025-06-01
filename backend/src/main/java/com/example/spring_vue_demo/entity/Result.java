package com.example.spring_vue_demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/05/24
 */
@Data
@AllArgsConstructor
public class Result {
    @Schema(description = "响应码")
    private Integer code;//响应码，1 代表成功; 0 代表失败

    @Schema(description = "响应信息")
    private String msg;  //响应信息 描述字符串

    @Schema(description = "返回的数据")
    private Object data; //返回的数据

    //成功响应(无数据）
    public static Result success(){
        return new Result(1,"success",null);
    }
    //成功响应（带数据）
    public static Result success(Object data){
        return new Result(1,"success",data);
    }

    //失败响应
    public static Result error(String msg){
        return new Result(0,msg,null);
    }
    //失败响应（自定义异常）
    public static Result error(Integer code,String msg){
        return new Result(code,msg,null);
    }
}
