package com.zlmthy.router;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.AsciiString;

import static io.netty.util.internal.MathUtil.findNextPositivePowerOfTwo;
import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * @author zengliming
 * @ClassName MyHttpMethod
 * @Description TODO
 * @date 2018/9/28 14:26
 */
public class MyHttpMethod implements Comparable<HttpMethod>{

    public static final MyHttpMethod GET = new MyHttpMethod("GET");

    public static final MyHttpMethod POST = new MyHttpMethod("POST");

    private final AsciiString name;

    public MyHttpMethod(String name) {
        name = checkNotNull(name, "name").trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }

        for (int i = 0; i < name.length(); i ++) {
            char c = name.charAt(i);
            if (Character.isISOControl(c) || Character.isWhitespace(c)) {
                throw new IllegalArgumentException("invalid character in name");
            }
        }

        this.name = AsciiString.cached(name);
    }

    private static final MyHttpMethod.EnumNameMap<MyHttpMethod> methodMap;

    static {
        methodMap = new MyHttpMethod.EnumNameMap<MyHttpMethod>(
                new MyHttpMethod.EnumNameMap.Node<MyHttpMethod>(GET.toString(), GET),
                new MyHttpMethod.EnumNameMap.Node<MyHttpMethod>(POST.toString(), POST)
        );
    }

    public String name() {
        return name.toString();
    }


    @Override
    public int hashCode() {
        return name().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HttpMethod)) {
            return false;
        }

        HttpMethod that = (HttpMethod) o;
        return name().equals(that.name());
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public int compareTo(HttpMethod o) {
        return name().compareTo(o.name());
    }


    private static final  class EnumNameMap<T>{

        private final MyHttpMethod.EnumNameMap.Node<T>[] values;
        private final int valuesMask;

        EnumNameMap(MyHttpMethod.EnumNameMap.Node<T>... nodes) {
            values = (MyHttpMethod.EnumNameMap.Node<T>[]) new MyHttpMethod.EnumNameMap.Node[findNextPositivePowerOfTwo(nodes.length)];
            valuesMask = values.length - 1;
            for (MyHttpMethod.EnumNameMap.Node<T> node : nodes) {
                int i = hashCode(node.key) & valuesMask;
                if (values[i] != null) {
                    throw new IllegalArgumentException("index " + i + " collision between values: [" +
                            values[i].key + ", " + node.key + ']');
                }
                values[i] = node;
            }
        }

        T get(String name) {
            MyHttpMethod.EnumNameMap.Node<T> node = values[hashCode(name) & valuesMask];
            return node == null || !node.key.equals(name) ? null : node.value;
        }

        private static int hashCode(String name) {
            return name.hashCode() >>> 6;
        }


        private static final class Node<T> {
            final String key;
            final T value;

            Node(String key, T value) {
                this.key = key;
                this.value = value;
            }
        }


    }

}
