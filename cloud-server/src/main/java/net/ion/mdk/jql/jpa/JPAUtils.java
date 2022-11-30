package net.ion.mdk.jql.jpa;

import net.ion.mdk.util.ClassUtils;

import javax.persistence.Id;
import java.lang.reflect.Field;

public interface JPAUtils {
    static Field findIdField(Class<?> clazz) {
        Field f = ClassUtils.findAnnotatedField(clazz, Id.class);
        if (f == null) {
            f = ClassUtils.findAnnotatedField(clazz, org.springframework.data.annotation.Id.class);
        }
        return f;
    }
}
