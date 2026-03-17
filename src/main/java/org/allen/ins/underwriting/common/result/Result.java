package org.allen.ins.underwriting.common.result;

import cn.hutool.http.HttpStatus;
import lombok.Data;

import java.io.Serializable;

/**
 * 全局统一返回结果
 * 前端可根据code判断是否成功，msg返回提示，data返回业务数据
 */
@Data
public class Result<T> implements Serializable {
    /**
     * 响应码：200成功，500失败，400参数错误，404资源不存在
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String msg;
    /**
     * 响应数据
     */
    private T data;

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(HttpStatus.HTTP_OK);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败响应
    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(HttpStatus.HTTP_INTERNAL_ERROR);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    // 失败响应
    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    // 参数错误响应
    public static <T> Result<T> paramError(String msg) {
        Result<T> result = new Result<>();
        result.setCode(HttpStatus.HTTP_BAD_REQUEST);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    public static <T> Result<T> notFound(String msg) {
        Result<T> result = new Result<>();
        result.setCode(HttpStatus.HTTP_NOT_FOUND);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}