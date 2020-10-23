package com.github.zlmisme.utils;

import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * 参数工具类
 *
 * @author zengliming
 * @ClassName ParameterNameUtil
 * @Description TODO
 * @date 2018/10/8 15:57
 */
public class ParameterNameUtil {

    /**
     * 利用ASM获取方法参数的名称
     *
     * @param clazz
     * @param method
     * @return
     */
    public static String[] getMethodParameterNameByAsm(Class<?> clazz, Method method) {

        // 获取所有参数的类型
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length == 0) {
            return null;
        }
        // 初始化Types
        final Type[] types = new Type[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            types[i] = Type.getType(parameterTypes[i]);
        }

        // 初始化数组存放参数名称
        final String[] parameters = new String[parameterTypes.length];

        String className = clazz.getName();

        int lastIndex = className.lastIndexOf(".");

        InputStream inputStream = clazz.getResourceAsStream(className.substring(lastIndex + 1) + ".class");

        try {
            ClassReader classReader = new ClassReader(inputStream);
            classReader.accept(new ClassVisitor(Opcodes.ASM4) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                    Type[] argumentTypes = Type.getArgumentTypes(desc);
                    // 判断方法名和方法参数是不是一致
                    if (!method.getName().equals(name) || !Arrays.equals(argumentTypes, types)) {
                        return null;
                    }

                    return new MethodVisitor(Opcodes.ASM4) {
                        @Override
                        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                            if (Modifier.isStatic(method.getModifiers())) {
                                parameters[index] = name;
                            } else if (index > 0) {
                                parameters[index - 1] = name;
                            }
                        }
                    };

                }
            }, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parameters;
    }

    public static void fillList(List<Object> list, Class<?> parameter, Object value) {
        System.out.println("value" + value);
        if ("java.lang.String".equals(parameter.getTypeName())) {
            list.add(value);
        } else if ("java.lang.Character".equals(parameter.getTypeName())) {
            char[] ch = ((String) value).toCharArray();
            list.add(ch[0]);
        } else if ("char".equals(parameter.getTypeName())) {
            char[] ch = ((String) value).toCharArray();
            list.add(ch[0]);
        } else if ("java.lang.Double".equals(parameter.getTypeName())) {
            list.add(Double.parseDouble((String) value));
        } else if ("double".equals(parameter.getTypeName())) {
            list.add(Double.parseDouble((String) value));
        } else if ("java.lang.Integer".equals(parameter.getTypeName())) {
            list.add(Integer.parseInt((String) value));
        } else if ("int".equals(parameter.getTypeName())) {
            list.add(Integer.parseInt((String) value));
        } else if ("java.lang.Long".equals(parameter.getTypeName())) {
            list.add(Long.parseLong((String) value));
        } else if ("long".equals(parameter.getTypeName())) {
            list.add(Long.parseLong((String) value));
        } else if ("java.lang.Float".equals(parameter.getTypeName())) {
            list.add(Float.parseFloat((String) value));
        } else if ("float".equals(parameter.getTypeName())) {
            list.add(Float.parseFloat((String) value));
        } else if ("java.lang.Short".equals(parameter.getTypeName())) {
            list.add(Short.parseShort((String) value));
        } else if ("shrot".equals(parameter.getTypeName())) {
            list.add(Short.parseShort((String) value));
        } else if ("java.lang.Byte".equals(parameter.getTypeName())) {
            list.add(Byte.parseByte((String) value));
        } else if ("byte".equals(parameter.getTypeName())) {
            list.add(Byte.parseByte((String) value));
        } else if ("java.lang.Boolean".equals(parameter.getTypeName())) {
            if ("false".equals(value) || "0".equals(value)) {
                list.add(false);
            } else if ("true".equals(value) || "1".equals(value)) {
                list.add(true);
            }
        } else if ("boolean".equals(parameter.getTypeName())) {
            if ("false".equals(value) || "0".equals(value)) {
                list.add(false);
            } else if ("true".equals(value) || "1".equals(value)) {
                list.add(true);
            }
        }
    }
}
