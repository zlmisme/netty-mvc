package com.github.zlmisme.utils.log;

/**
 * @author zengliming
 * @date 22018-09-14 14:23
 */
public enum LogType {

    /**
     * handle日志
     */
    NETTY_HANDLE("NETTY_HANDLE"),
    /**
     * server日志
     */
    SERVER("SERVER"),
    /**
     * 框架日志
     */
    FRAMEWORK("FRAMEWORK"),
    /**
     * 工具类日志
     */
    UTILS("UTILS");

    LogType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
