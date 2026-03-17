package org.allen.ins.underwriting.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.allen.ins.underwriting.common.exception.BusinessException;
import org.allen.ins.underwriting.common.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一捕获所有Controller层抛出的异常，返回标准Result格式
 */
@Slf4j
@RestControllerAdvice // 等价于 @ControllerAdvice + @ResponseBody，返回JSON格式
public class GlobalExceptionHandler {

    // ========== 1. 处理自定义业务异常（优先级最高） ==========
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage()); // 业务异常只打warn日志，不打error
        return Result.fail(e.getCode(), e.getMessage()); // 用自定义的状态码和信息
    }

    // ========== 2. 处理参数校验异常（比如@Valid校验失败） ==========
    // 处理@RequestBody参数校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 拼接所有参数错误信息（比如：用户名不能为空，手机号格式错误）
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("，"));
        log.warn("参数校验异常：{}", msg);
        return Result.paramError(msg); // 对应400状态码
    }

    // 处理@RequestParam/表单参数校验失败
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("，"));
        log.warn("参数绑定异常：{}", msg);
        return Result.paramError(msg);
    }

    // ========== 3. 处理404异常（资源不存在） ==========
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        String msg = "请求路径不存在：" + e.getRequestURL();
        log.warn(msg);
        return Result.notFound(msg); // 对应404状态码
    }

    // ========== 4. 处理所有未捕获的通用异常（兜底） ==========
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e); // 系统异常打error日志，包含堆栈信息
        return Result.fail("系统繁忙，请稍后重试"); // 对外隐藏具体异常，只返回友好提示
    }
}