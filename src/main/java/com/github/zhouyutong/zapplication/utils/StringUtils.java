package com.github.zhouyutong.zapplication.utils;

import com.github.zhouyutong.zapplication.constant.SymbolConstant;
import com.google.common.base.Strings;

import java.util.Locale;
import java.util.Random;

/**
 * 字符串utils
 *
 * @author zhoutao
 */
public class StringUtils {

    private StringUtils() {
    }

    /**
     * 截取字符串,不抛出任何异常,要么返回""要么返回内容
     *
     * @param string
     * @param start  - 开始位置
     * @param end    - 结束位置
     * @return
     */
    public static String substr(String string, int start, int end) {
        if (Strings.isNullOrEmpty(string)) {
            return SymbolConstant.EMPTY;
        }
        try {
            if (string.length() <= end) {
                return string.substring(start);
            } else {
                return string.substring(start, end);
            }
        } catch (IndexOutOfBoundsException e) {
            ;
        }
        return SymbolConstant.EMPTY;
    }

    /**
     * 截取字符串,不抛出任何异常,要么返回""要么返回内容
     *
     * @param obj
     * @param start - 开始位置
     * @param end   - 结束位置
     * @return
     */
    public static String substr(Object obj, int start, int end) {
        return substr(obj == null ? SymbolConstant.EMPTY : obj.toString(), start, end);
    }

    /**
     * 生成n位数字随机码
     *
     * @return code
     */
    public static String geneVcode(int n) {
        Random random = new Random();
        StringBuilder nine = new StringBuilder();
        for (int i = 0; i < n; i++) {
            nine.append("9");
        }
        int randomNum = random.nextInt(Integer.parseInt(nine.toString()));
        String format = "%0" + n + "d";
        return String.format(format, randomNum);
    }

    /**
     * 驼峰法转换为全小写带下划线
     *
     * @param camelName
     * @return
     */
    public static String underscoreName(String camelName) {
        if (camelName == null) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();
            result.append(lowerCaseName(camelName.substring(0, 1)));

            for (int i = 1; i < camelName.length(); ++i) {
                String s = camelName.substring(i, i + 1);
                String slc = lowerCaseName(s);
                if (!s.equals(slc)) {
                    result.append("_").append(slc);
                } else {
                    result.append(s);
                }
            }

            return result.toString();
        }
    }

    private static String lowerCaseName(String name) {
        return name.toLowerCase(Locale.US);
    }

    public static boolean isBlank(CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isBlank(cs);
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

}
