package com.github.zlmisme.config;

import lombok.Data;

/**
 * 加载配置
 *
 * @author zengliming
 * @ClassName LoadConfig
 * @Description TODO
 * @date 2018/9/28 14:18
 */
@Data
public class LoadConfig {

    private static String basePackage = "com.github.zlmisme.example";

    public static String getBasePackage() {
        return basePackage;
    }

}
