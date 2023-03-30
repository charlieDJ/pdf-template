package com.dj.common.utils;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionUtils {
    /**
     * 获取对象的属性值
     *
     * @param fieldName 属性名
     * @param obj       对象
     * @return 属性值
     */
    public static Object getValue(String fieldName, Object obj) {
        Objects.requireNonNull(obj, "Class must not be null");
        Objects.requireNonNull(fieldName, "Name must not be null");
        Field field = findField(obj.getClass(), fieldName);
        if (null == field) {
            throw new RuntimeException("No Such field " + fieldName + " from class" + ClassUtils.getShortClassName(obj.getClass()));
        }
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取对象的属性值
     *
     * @param fieldName 属性名
     * @param obj       对象
     * @return 属性值
     */
    public static String getStrValue(String fieldName, Object obj) {
        Object value = getValue(fieldName, obj);
        if (value instanceof String) {
            return value.toString();
        }
        return "";
    }

    /**
     * 获取对象的属性值
     *
     * @param clazz 类
     * @return 属性列表
     */
    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Objects.requireNonNull(clazz, "Class must not be null");
        Class<?> searchType = clazz;
        // 从父类开始获取，直到Object
        while (Object.class != searchType && searchType != null) {
            List<Field> collect = Stream.of(searchType.getDeclaredFields()).collect(Collectors.toList());
            fields.addAll(collect);
            searchType = searchType.getSuperclass();
        }
        return fields;
    }

    /**
     * 获取对象的属性值
     *
     * @param clazz 类
     * @param name  属性名
     * @return 属性
     */
    public static Field findField(Class<?> clazz, String name) {
        Objects.requireNonNull(clazz, "Class must not be null");
        Objects.requireNonNull(name, "Name must not be null");
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field field;
            try {
                field = searchType.getDeclaredField(name);
                return field;
            } catch (NoSuchFieldException e) {
                // no-op
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * 获取对象的属性值
     *
     * @param clazz           类
     * @param parametersTypes 参数类型
     * @param <T>             类型
     * @return 构造器
     */
    public static <T> Constructor<T> findConstructor(Class<T> clazz, Class<?>... parametersTypes) throws Exception {
        Constructor<T> constructor = clazz.getDeclaredConstructor(parametersTypes);
        constructor.setAccessible(true);
        return constructor;
    }
}
