package com.github.zlmisme;

import com.github.zlmisme.oranges.commons.ModuleLoadService;

import java.util.ServiceLoader;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        final ServiceLoader<ModuleLoadService> moduleLoadServices = ServiceLoader.load(ModuleLoadService.class);
        moduleLoadServices.forEach(ModuleLoadService::load);
    }
}
