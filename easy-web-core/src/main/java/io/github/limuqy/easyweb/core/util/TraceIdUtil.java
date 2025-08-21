package io.github.limuqy.easyweb.core.util;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.DigestUtil;
import org.slf4j.MDC;

public class TraceIdUtil {
    public static final String TRACE_ID = "traceId";

    public static void randomTraceId() {
        setTraceId(DigestUtil.md5Hex16(UUID.fastUUID().toString(true)));
    }

    public static void setTraceId(String traceId) {
        MDC.put(TraceIdUtil.TRACE_ID, traceId);
    }

    public static String getTraceId() {
        return MDC.get(TraceIdUtil.TRACE_ID);
    }
}