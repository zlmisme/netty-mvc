package com.github.zlmisme.oranges.aop;

import com.github.zlmisme.oranges.commons.ModuleLoadService;

/**
 * spi 加载aop模块
 *
 * @author liming zeng
 * @create 2020-10-23 18:18
 */
public class AopModuleLoadServiceImpl implements ModuleLoadService {

    @Override
    public void load() {
        System.out.println("aop load");
    }
}
