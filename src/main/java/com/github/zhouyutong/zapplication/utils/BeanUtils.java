package com.github.zhouyutong.zapplication.utils;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanMap;

import java.util.Iterator;
import java.util.Map;

/**
 * 基于apache bean utils提供进一步的封装
 * Created by lianjia on 2016/7/7.
 */
public class BeanUtils {
    private BeanUtils() {
    }

    public static Map<String, Object> bean2Map(Object bean) {
        if (bean == null) {
            return null;
        }
        Map<String, Object> map = Maps.newHashMap();
        BeanMap beanMap = new BeanMap(bean);
        Iterator<String> keyIterator = beanMap.keyIterator();
        while (keyIterator.hasNext()) {
            String property = keyIterator.next();
            if ("class".equals(property)) {
                continue;
            }
            Object value = beanMap.get(property);
            map.put(property, value);
        }
        return map;
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        if (map == null || map.isEmpty() || beanClass == null) {
            return null;
        }

        try {
            T obj = initClass(beanClass);
            org.apache.commons.beanutils.BeanUtils.copyProperties(obj, map);
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T initClass(Class<T> beanClass) throws IllegalAccessException, InstantiationException {
        return beanClass.newInstance();
    }

    public static void copyProperties(Object dest, Object orig) {
        if (dest == null) {
            throw new RuntimeException("copyProperties error,第一个参数目标对象为NULL");
        }
        if (orig == null) {
            throw new RuntimeException("copyProperties error,第二个参数源对象为NULL");
        }
        try {
            org.apache.commons.beanutils.BeanUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            throw new RuntimeException("copyProperties error", e);
        }
    }
}
