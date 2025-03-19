package com.example.TaskManagement.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class BeanUtils {
    @SneakyThrows
    public void copyNonNullProperties(Object source, Object destination) {
        Class<?> clazz = source.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(source);

            if (value != null) {
                field.set(destination, value);
            }
        }
    }
}
