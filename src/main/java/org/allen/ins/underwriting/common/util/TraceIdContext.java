package org.allen.ins.underwriting.common.util;

import java.util.UUID;

/**
 * 全链路追踪ID工具类
 * 基于ThreadLocal存储，保证单请求内traceId全局唯一且线程隔离
 */
public class TraceIdContext {
    // ThreadLocal存储traceId，泛型为String（UUID/雪花算法都适配）
    private static final ThreadLocal<String> TRACE_ID_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 生成全局唯一traceId（默认用UUID，也可替换为雪花算法）
     */
    public static String generateTraceId() {
        // UUID去掉横线，缩短长度（也可直接用UUID.randomUUID().toString()）
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 设置traceId到ThreadLocal
     */
    public static void setTraceId(String traceId) {
        TRACE_ID_THREAD_LOCAL.set(traceId);
    }

    /**
     * 获取当前线程的traceId
     */
    public static String getTraceId() {
        return TRACE_ID_THREAD_LOCAL.get();
    }

    /**
     * 移除ThreadLocal中的traceId（必须调用，避免内存泄漏）
     */
    public static void removeTraceId() {
        TRACE_ID_THREAD_LOCAL.remove();
    }
}