package com.zhouyutong.zapplication.serialization.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <b>json解析库fastjson的简单封装</b><br>
 *
 * @author zhoutao
 * @thread-safe
 * @since 2015-11-2
 */
public final class FastJson {
    //不忽略NULL值,一般在转json字符串要落地NoSql的时候使用，否则会存在null值的字段不存在的情况
    private static SerializerFeature[] WRITE_NULL_VALUE_SERIALIZER_FEATURES = new SerializerFeature[]{
            SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteNullNumberAsZero,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullBooleanAsFalse
    };

    private FastJson() {
    }

    /**
     * json字符串到对象的转换
     *
     * @param jsonStr
     * @return
     * @throws JsonException
     */
    public static <T> T jsonStr2Object(String jsonStr, Class<T> valueType) {
        checkNotNull(jsonStr);

        try {
            return JSON.parseObject(jsonStr, valueType);
        } catch (Exception e) {
            throw new JsonException("FastJson.jsonStr2Object error.", e);
        }
    }

    /**
     * 对象到json字符串的转换
     *
     * @param obj
     * @return
     * @throws JsonException
     */
    public static String object2JsonStr(Object obj) {
        checkNotNull(obj);

        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            throw new JsonException("FastJson.object2JsonStr error.", e);
        }
    }

    /**
     * 对象到json字符串的转换
     *
     * @param obj
     * @param ignoreFieldNameList - 需要忽略的字段
     * @return
     * @throws JsonException
     */
    public static String object2JsonStrForIgnore(Object obj, List<String> ignoreFieldNameList) {
        checkNotNull(obj);

        try {
            return JSON.toJSONString(obj, new IgnoreFieldNameFilter(ignoreFieldNameList));
        } catch (Exception e) {
            throw new JsonException("FastJson.object2JsonStr error, ignoreFieldNameList:" + ignoreFieldNameList, e);
        }
    }

    /**
     * 对象到json字符串的转换
     *
     * @param obj
     * @param includeFieldNameList - 需要包含的字段
     * @return
     * @throws JsonException
     */
    public static String object2JsonStrForInclude(Object obj, List<String> includeFieldNameList) {
        checkNotNull(obj);

        try {
            return JSON.toJSONString(obj, new IncludeFieldNameFilter(includeFieldNameList));
        } catch (Exception e) {
            throw new JsonException("FastJson.object2JsonStrForInclude error, includeFieldNameList:" + includeFieldNameList, e);
        }
    }

    /**
     * 对象到json字符串的转换
     *
     * @param obj
     * @return
     * @throws JsonException
     */
    public static String object2JsonStrUseNullValue(Object obj) {
        checkNotNull(obj);

        try {
            return JSON.toJSONString(obj, WRITE_NULL_VALUE_SERIALIZER_FEATURES);
        } catch (Exception e) {
            throw new JsonException("FastJson.object2JsonStrUseNullValue error.", e);
        }
    }

    /**
     * 对象到json字符串的转换
     *
     * @param obj
     * @param ignoreFieldNameList - 需要忽略的字段
     * @return
     * @throws JsonException
     */
    public static String object2JsonStrForIgnoreUseNullValue(Object obj, List<String> ignoreFieldNameList) {
        checkNotNull(obj);

        try {
            return JSON.toJSONString(obj, new IgnoreFieldNameFilter(ignoreFieldNameList), WRITE_NULL_VALUE_SERIALIZER_FEATURES);
        } catch (Exception e) {
            throw new JsonException("FastJson.object2JsonStrForIgnoreUseNullValue error, ignoreFieldNameList:" + ignoreFieldNameList, e);
        }
    }

    /**
     * 对象到json字符串的转换
     *
     * @param obj
     * @param includeFieldNameList - 需要包含的字段
     * @return
     * @throws JsonException
     */
    public static String object2JsonStrForIncludeUseNullValue(Object obj, List<String> includeFieldNameList) {
        checkNotNull(obj);

        try {
            return JSON.toJSONString(obj, new IncludeFieldNameFilter(includeFieldNameList), WRITE_NULL_VALUE_SERIALIZER_FEATURES);
        } catch (Exception e) {
            throw new JsonException("FastJson.object2JsonStrForIncludeUseNullValue error, includeFieldNameList:" + includeFieldNameList, e);
        }
    }

    private static class IgnoreFieldNameFilter implements PropertyFilter {
        private List<String> ignoreFieldNameList;

        public IgnoreFieldNameFilter(List<String> ignoreFieldNameList) {
            this.ignoreFieldNameList = ignoreFieldNameList;
        }

        @Override
        public boolean apply(Object source, String name, Object value) {
            if (ignoreFieldNameList.contains(name)) {
                return false;
            }
            return true;
        }
    }

    private static class IncludeFieldNameFilter implements PropertyFilter {
        private List<String> includeFieldNameList;

        public IncludeFieldNameFilter(List<String> includeFieldNameList) {
            this.includeFieldNameList = includeFieldNameList;
        }

        @Override
        public boolean apply(Object source, String name, Object value) {
            if (includeFieldNameList.contains(name)) {
                return true;
            }
            return false;
        }
    }
}
