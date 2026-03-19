package org.allen.ins.underwriting.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.allen.ins.underwriting.common.util.TraceIdContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 全局TraceId拦截器
 * 所有核保/风控相关请求进入时生成traceId，请求结束后清理
 */
public class TraceIdInterceptor implements HandlerInterceptor {

    /**
     * 请求处理前：生成traceId并放入ThreadLocal
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 1. 生成traceId
        String traceId = TraceIdContext.generateTraceId();
        // 2. 放入ThreadLocal
        TraceIdContext.setTraceId(traceId);
        // 3. 将traceId放入响应头，方便前端/网关查看
        response.setHeader("X-Trace-Id", traceId);
        // 放行请求
        return true;
    }

    /**
     * 请求处理完成后（无论成功/失败）：移除ThreadLocal中的traceId，避免内存泄漏
     */
    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        TraceIdContext.removeTraceId();
    }
}