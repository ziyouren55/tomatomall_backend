package com.example.tomatomall.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class MyBeanUtil {

    /**
     * 获取 source 对象中为 null 的属性名称数组
     *
     * @param source 源对象
     * @return 为 null 的属性名称数组
     */
    public static String[] getNullPropertyNames(Object source) {
        if (source == null) {
            return new String[0];
        }
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            String propertyName = pd.getName();
            // 过滤掉 "class" 属性
            if ("class".equals(propertyName)) {
                continue;
            }
            Object srcValue = src.getPropertyValue(propertyName);
            if (srcValue == null) {
                emptyNames.add(propertyName);
            }
        }
        return emptyNames.toArray(new String[0]);
    }
}
