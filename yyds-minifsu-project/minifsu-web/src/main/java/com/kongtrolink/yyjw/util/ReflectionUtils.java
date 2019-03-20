/**
 * Copyright (c) 2005-2009 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * $Id: ReflectionUtils.java,v 1.1 2013/10/30 01:50:25 liuj Exp $
 */
package com.kongtrolink.yyjw.util;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.*;
import java.util.*;

/**
 * 反射工具类.
 * <p/>
 * 提供访问私有变量,获取泛型类型Class, 提取集合中元素的属性, 转换字符串到对象等Util函数.
 *
 * @author calvin
 */
public class ReflectionUtils
{

    private static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    static {
        DateConverter dc = new DateConverter();
        dc.setUseLocaleFormat(true);
        dc.setPatterns(new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"});
        ConvertUtils.register(dc, Date.class);
    }

    /**
     * 调用Getter方法.
     */
    public static Object invokeGetterMethod(Object target, String propertyName) {
        String getterMethodName = "get" + StringUtils.capitalize(propertyName);
        return invokeMethod(target, getterMethodName, new Class[]{}, new Object[]{});
    }

    /**
     * 调用Setter方法.使用value的Class来查找Setter方法.
     */
    public static void invokeSetterMethod(Object target, String propertyName, Object value) {
        invokeSetterMethod(target, propertyName, value, null);
    }

    /**
     * 调用Setter方法.
     *
     * @param propertyType 用于查找Setter方法,为空时使用value的Class替代.
     */
    public static void invokeSetterMethod(Object target, String propertyName, Object value, Class<?> propertyType) {
        Class<?> type = propertyType != null ? propertyType : value.getClass();
        String setterMethodName = "set" + StringUtils.capitalize(propertyName);
        invokeMethod(target, setterMethodName, new Class[]{type}, new Object[]{value});
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     */
    public static Object getFieldValue(final Object object, final String fieldName) {
        Field field = getDeclaredField(object, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }

        makeAccessible(field);

        Object result = null;
        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static void setFieldValue(final Object object, final String fieldName, final Object value) {
        Field field = getDeclaredField(object, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }

        makeAccessible(field);

        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常:{}", e.getMessage());
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     */
    public static Object invokeMethod(final Object object, final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] parameters) {
        Method method = getDeclaredMethod(object, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
        }

        method.setAccessible(true);

        try {
            return method.invoke(object, parameters);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField.<br/>
     * <p/>
     * 如向上转型到Object仍无法找到, 返回null.<br/>
     */
    protected static Field getDeclaredField(final Object object, final String fieldName) {
        Assert.notNull(object, "object不能为空");
        Assert.hasText(fieldName, "fieldName");
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {//NOSONAR
                // Field不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 强行设置Field可访问.<br/>
     */
    protected static void makeAccessible(final Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod.<br/>
     * <p/>
     * 如向上转型到Object仍无法找到, 返回null.<br/>
     */
    protected static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {
        Assert.notNull(object, "object不能为空");

        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                return superClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {//NOSONAR
                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.<br/>
     * 如无法找到, 返回Object.class.<br/>
     * eg.
     * public UserDao extends HibernateDao<User>
     *
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be determined
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得定义Class时声明的父类的泛型参数的类型.<br/>
     * 如无法找到, 返回Object.class.<br/>
     * <p/>
     * 如public UserDao extends HibernateDao<User,Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined
     */
    @SuppressWarnings("unchecked")
    public static Class getSuperClassGenricType(final Class clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }

    /**
     * 提取集合中的对象的属性(通过getter函数), 组合成List.<br/>
     *
     * @param collection   来源集合.<br/>
     * @param propertyName 要提取的属性名.<br/>
     */
    @SuppressWarnings("unchecked")
    public static List convertElementPropertyToList(final Collection collection, final String propertyName) {
        List list = new ArrayList();

        try {
            for (Object obj : collection) {
                list.add(PropertyUtils.getProperty(obj, propertyName));
            }
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }

        return list;
    }

    /**
     * 提取集合中的对象的属性(通过getter函数), 组合成由分割符分隔的字符串.<br/>
     *
     * @param collection   来源集合.<br/>
     * @param propertyName 要提取的属性名.<br/>
     * @param separator    分隔符.<br/>
     */
    @SuppressWarnings("unchecked")
    public static String convertElementPropertyToString(final Collection collection, final String propertyName,
                                                        final String separator) {
        List list = convertElementPropertyToList(collection, propertyName);
        return StringUtils.join(list, separator);
    }

    /**
     * 提取集合中的对象的属性(通过getter函数), 组合成由分割符分隔的字符串.<br/>
     *
     * @param collection   来源集合.<br/>
     * @param propertyName 作为key的属性名.<br/>
     */
    @SuppressWarnings("unchecked")
    public static Map convertToMapWithElementProperty(final Collection collection, final String propertyName) {
        Map elementMap = new LinkedHashMap();
        try {
            for (Object obj : collection) {
                elementMap.put(PropertyUtils.getProperty(obj, propertyName), obj);
            }
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
        return elementMap;
    }

    /**
     * 转换字符串到相应类型.<br/>
     *
     * @param value  待转换的字符串<br/>
     * @param toType 转换目标类型<br/>
     */
    public static Object convertStringToObject(String value, Class<?> toType) {
        try {
            return ConvertUtils.convert(value, toType);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 将反射时的checked exception转换为unchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException("Reflection Exception.", e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException("Reflection Exception.", ((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception.", e);
    }

    /**
     * 将字符串转换为Set<br/>
     * @param s 需要转换的字符串<br/>
     * @param seperator 分隔符<br/>
     * @return
     */
    public static Set<String> makeSet(String s, String seperator) {
        Set<String> strSet = new LinkedHashSet<String>();
        if (s == null) return strSet;
        else {
            String[] ss = s.split(seperator);
            List<String> sList = Arrays.asList(ss);
            strSet.addAll(sList);
        }
        return strSet;
    }

    /**
     * 需要转换的字符串，默认分隔符为逗号<br/>
     * @param s
     * @return
     */
    public static Set<String> makeSet(String s) {
        return makeSet(s, ",");
    }

    /**
     * 将任意集合的某个属性转换为SET<br/>
     * @param collection 需要转换的集合<br/>
     * @param propertyName 参与转换的属性<br/>
     * @return
     */
    public static <T> Set<String> makeSet(Collection<T> collection, String propertyName) {
        Set<String> strSet = new LinkedHashSet<String>();

        try {
            for (T t : collection) {
                strSet.add((String) PropertyUtils.getProperty(t, propertyName));
            }
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
        return strSet;
    }

    /**
     * 比较两个SET，返回各自包含相同数据的个数<br/>
     * @param set1 参与比较的SET<br/>
     * @param set2 参与比较的SET<br/>
     * @return
     */
    public static int compareSet(Set set1,Set set2){
        int sameCount = 0;
        for(Object o : set1){
            if(set2.contains(o)) sameCount++;
        }
        return sameCount;
    }

    /**
     * 将字符串的集合转换为用分隔符分隔的字符串
     * @param collectionString
     * @param delemeter
     * @return
     */
    public static String convertStringCollectionToString(Collection<String> collectionString,String delemeter) {
        StringBuilder sb = new StringBuilder("");
        for(String s : collectionString){
            if(sb.length()!=0){
                sb.append(delemeter);
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
