package com.zlmthy.context;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * @author zengliming
 * @date 2020/3/30
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public class ThreadLocalHolder {


    private static final FastThreadLocal<IContext> CONTEXT = new FastThreadLocal();

    /**
     * set context
     *
     * @param context current context
     */
    public static void setContext(IContext context) {
        CONTEXT.set(context);
    }

    /**
     * remove context
     */
    public static void removeContext() {
        CONTEXT.remove();

    }

    /**
     * @return get context
     */
    public static IContext getContext() {
        return CONTEXT.get();
    }
}
