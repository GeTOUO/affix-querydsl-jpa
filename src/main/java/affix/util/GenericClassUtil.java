package affix.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class GenericClassUtil {

    public static Class<?> getGenericClass(Class<?> targetClass, Class<?> filter) {
        Class<?> genericClass = null;
        Type genericSuperclass = targetClass.getGenericSuperclass();
        genericClass = getSomeType(genericSuperclass, filter).orElse(null);
        Type[] genericInterfaces = targetClass.getGenericInterfaces();
        for (int i = 0; i < genericInterfaces.length && genericClass == null; i++) {
            genericClass = getSomeType(genericInterfaces[i], filter).orElse(null);
        }
        return genericClass;
    }

    public static Optional<Class<?>> getSomeType(Type genericClz, Class filter) {
        if (genericClz instanceof ParameterizedType) {
            ParameterizedType clzType = (ParameterizedType) genericClz;
            Type[] actualTypeArguments = clzType.getActualTypeArguments();
            for (Type actualTypeArgument: actualTypeArguments) {
                if (actualTypeArgument instanceof Class && filter.isAssignableFrom((Class) actualTypeArgument)) {
                    return Optional.ofNullable((Class) actualTypeArgument);
                }
            }
        }
        return Optional.empty();
    }
}
