package com.zlmthy.utils;

import com.zlmthy.utils.log.LogType;
import com.zlmthy.utils.log.LogUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * @author zengliming
 * @ClassName ClassUtil
 * @Description class工具类
 * @date 2018/9/6 9:16
 */
public class ClassUtil {

    private static LogUtil log = LogUtil.getLog(LogType.UTILS);

    /**
     * 通过包名获取包内所有类
     *
     * @param packageName 包路径
     * @return 通过包名获取包内所有类
     */
    public static List<Class<?>> getAllClassByPackageName(String packageName) {
        // 获取当前包下以及子包下所以的类
        return getClasses(packageName, null);
    }

    /**
     * 通过接口名取得某个接口下所有实现这个接口的类
     *
     * @param c 接口
     * @return 接口包下实现该接口的所有类
     */
    public static List<Class<?>> getAllClassByInterface(Class<?> c) {
        List<Class<?>> returnClassList = null;

        if (c.isInterface()) {
            // 获取当前的包名
            String packageName = c.getPackage().getName();
            // 获取当前包下以及子包下所以的类
            List<Class<?>> allClass = getClasses(packageName, null);
            if (allClass != null) {
                returnClassList = new ArrayList<Class<?>>();
                for (Class<?> cls : allClass) {
                    // 判断是否是同一个接口
                    if (c.isAssignableFrom(cls)) {
                        // 本身不加入进去
                        if (!c.equals(cls)) {
                            returnClassList.add(cls);
                        }
                    }
                }
            }
        }
        return returnClassList;
    }

    /**
     * 通过接口名取得某个接口下所有实现这个接口的类
     *
     * @param packageName 接口
     * @param annotation  接口
     * @return 接口包下实现该接口的所有类
     */
    public static List<Class<?>> getAllClassByPackageNameAndAnnotation(String packageName, Class<? extends Annotation> annotation) {
        if (annotation !=null && annotation.isAnnotation()) {
            return getClasses(packageName, annotation);
        }
        return getClasses(packageName, null);
    }


    /**
     * 取得某一类所在包的所有类名 不含迭代
     *
     * @param classLocation 源文件目录
     * @param packageName   包
     */
    public static String[] getPackageAllClassName(String classLocation, String packageName) {
        // 将packageName分解
        String[] packagePathSplit = packageName.split("[.]");
        StringBuilder sb = new StringBuilder(classLocation);
        // 获取当前包在当前系统的路径
        for (String str : packagePathSplit) {
            // File.separator 系统目录分隔符
            sb.append(File.separator).append(str);
        }
        // 根据当前系统所生成的路径获取目录对象
        File packageDir = new File(sb.toString());
        if (packageDir.isDirectory()) {
            return packageDir.list();
        } else {
            log.info("不是一个目录");
            return null;
        }
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packageName 包
     * @return package中获取所有的Class
     */
    private static List<Class<?>> getClasses(String packageName, Class<? extends Annotation> annotation) {

        // 第一个class类的集合
        List<Class<?>> classes = new ArrayList<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            Class<?> clazz = Class.forName(packageName + '.' + className);
                                            if (annotation == null || clazz.getAnnotation(annotation) != null || clazz.isAnnotationPresent(annotation)) {
                                                // 添加到classes
                                                classes.add(clazz);
                                            }
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        log.error("根据包获取JarFile里面的class,异常信息{0}", e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error("根据包获取File里面的class,异常信息{0}", e.getMessage());
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        if (dirfiles == null) {
            log.info("包下没有文件和目录");
            return;
        }
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    log.error("Class没有找到, 请检查扫描路径, 异常信息{0}", e);
                }
            }
        }
    }


}
