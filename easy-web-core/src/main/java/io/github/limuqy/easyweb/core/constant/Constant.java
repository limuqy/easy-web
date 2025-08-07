package io.github.limuqy.easyweb.core.constant;

public class Constant {
    /**
     * 身份认证请求头
     */
    public static final String HEADER_TOKEN = "x-token";
    /**
     * 匿名用户默认名称
     */
    public static final String ANONYMOUS_USER_NAME = "anonymous";
    public static final String ANONYMOUS_USER_CODE = "anonymous";

    public static final String Y = "Y";

    public static final String N = "N";
    public static final String EMPTY = "";
    public static final String ALL = "ALL";

    /**
     * 删除标识位
     */
    public static final Long DELETED_FLAG = 1L;

    /**
     * 未删除标识位
     */
    public static final Long UNDELETED_FLAG = 0L;
    public static final String OK = "OK";

    public static final String DEFAULT_REDIS_MODULE_NAME = "app";
}
