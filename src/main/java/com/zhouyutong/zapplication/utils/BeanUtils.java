package com.zhouyutong.zapplication.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Iterator;
import java.util.Map;

public class BeanUtils {
	private BeanUtils() {
	}

	public static MultiValueMap<String, Object> bean2MultiValueMap(Object bean) {
		if (bean == null) {
			return null;
		}
		MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap();
		BeanMap beanMap = new BeanMap(bean);

		Iterator<Map.Entry<Object, Object>> entryIterator = beanMap.entryIterator();
		while (entryIterator.hasNext()) {
			Map.Entry<Object, Object> entry = entryIterator.next();
			String key = entry.getKey().toString();
			if (!"class".equals(key)) {
				multiValueMap.put(key, Lists.newArrayList(entry.getValue()));
			}
		}
		return multiValueMap;
	}

	public static Map<String, Object> bean2Map(Object bean) {
		if (bean == null) {
			return null;
		}
		Map<String, Object> map = Maps.newHashMap();
		BeanMap beanMap = new BeanMap(bean);

		Iterator<Map.Entry<Object, Object>> entryIterator = beanMap.entryIterator();
		while (entryIterator.hasNext()) {
			Map.Entry<Object, Object> entry = entryIterator.next();
			String key = entry.getKey().toString();
			if ("class".equals(key)) {
				continue;
			}
			map.put(key, entry.getValue());
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
