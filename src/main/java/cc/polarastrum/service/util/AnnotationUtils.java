package cc.polarastrum.service.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * PolarAstrumService
 * cc.polarastrum.service.util.AnnotationUtils
 *
 * @author mical
 * @since 2025/2/15 12:18
 */
public class AnnotationUtils {

    public static <A extends Annotation> A createAnnotation(
            final Class<A> annotationType,
            final Map<String, Object> values
    ) {
        return (A) Proxy.newProxyInstance(
                annotationType.getClassLoader(),
                new Class[]{annotationType},
                new AnnotationInvocationHandler(annotationType, values)
        );
    }

    // 自定义 InvocationHandler
    static class AnnotationInvocationHandler implements InvocationHandler {
        private final Class<? extends Annotation> annotationType;
        private final Map<String, Object> values;

        public AnnotationInvocationHandler(
                final Class<? extends Annotation> annotationType,
                final Map<String, Object> values
        ) {
            this.annotationType = annotationType;
            this.values = values;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) {
            String methodName = method.getName();
            if (methodName.equals("annotationType")) {
                return annotationType; // 处理 annotationType() 方法
            }
            if (values.containsKey(methodName)) {
                return values.get(methodName); // 返回自定义值
            }
            return method.getDefaultValue(); // 返回注解定义的默认值
        }
    }
}
