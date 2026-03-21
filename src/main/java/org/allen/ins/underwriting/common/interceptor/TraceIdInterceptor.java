package org.allen.ins.underwriting.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.allen.ins.underwriting.common.util.TraceIdContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 全局TraceId拦截器
 * 所有核保/风控相关请求进入时生成traceId，请求结束后清理
 */
public class TraceIdInterceptor implements HandlerInterceptor {

    // 定义traceId的请求头/响应头名称（统一常量，便于维护）
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * 请求处理前：生成traceId并放入ThreadLocal
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 1. 从请求头获取traceId
        String traceId = request.getHeader(TRACE_ID_HEADER);

        // 2. 若请求头无有效traceId，生成新的
        if (!StringUtils.hasText(traceId)) {
            traceId = TraceIdContext.generateTraceId();
        }

        // 3. 生成traceId
        TraceIdContext.generateTraceId();

        // 4. 放入ThreadLocal
        TraceIdContext.setTraceId(traceId);

        // 5. 将traceId放入响应头，方便前端/网关查看
        response.setHeader(TRACE_ID_HEADER, traceId);

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