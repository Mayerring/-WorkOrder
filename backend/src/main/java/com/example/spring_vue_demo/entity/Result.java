package com.example.spring_vue_demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wtt
 * @date 2025/05/24
 */
@Data
@AllArgsConstructor
public class Result{
    @Schema(description = "响应码")
    private Integer code;//响应码，1 代表成功; 0 代表失败
    private String msg;  //响应信息 描述字符串
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
}
