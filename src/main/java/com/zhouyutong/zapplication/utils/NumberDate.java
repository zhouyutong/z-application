package com.zhouyutong.zapplication.utils;

import com.google.common.base.Preconditions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 数字可视化日期时间类型包装类
 * 提供基于数字可视化时间的基本运算如比较大小、转换等操作
 *
 * @Author zhoutao
 * @Date 2016/10/14
 */
public class NumberDate {
    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FORMAT_YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    public static final String FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String[] SUPPORT_FORMAT_ARRAY = new String[]{
            FORMAT_YYYY_MM_DD,
            FORMAT_YYYY_MM_DD_HH,
            FORMAT_YYYY_MM_DD_HH_MM,
            FORMAT_YYYY_MM_DD_HH_MM_SS,
            FORMAT_YYYY_MM_DD_HH_MM_SS_SSS
    };

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    /**
     * 转换和比较时使用的枚举类型
     */
    public enum TransType {
        MILLISECOND, SECOND, MINUTE, HOUR, DAY, MONTH, YEAR
    }

    /**
     * 支持格式：
     * yyyyMMddHHmmssSSS - 17位
     * yyyyMMddHHmmss - 14位
     * yyyyMMddHHmm - 12位
     * yyyyMMddHH - 10位
     * yyyyMMdd - 8位
     * yyyyMM - 6位
     * yyyy - 4位
     */
    private long numberTimes;

    public long getNumberTimes() {
        return numberTimes;
    }

    private NumberDate(long numberTimes) {
        String numberTimesStr = String.valueOf(numberTimes);
        int length = numberTimesStr.length();
        if (length < 8) {
            throw new IllegalArgumentException("numberTimes必须至少是8位数字：如20111020表示yyyyMMdd");
        }
        if (length > 17) {
            throw new IllegalArgumentException("numberTimes最多是17位数字：如20111020101010555表示yyyyMMddHHmmssSSS");
        }
        StringBuilder sb = new StringBuilder(numberTimesStr);
        int addZeroLength = 17 - length; //后补0的个数
        for (int i = 0; i < addZeroLength; i++) {
            sb.append("0");
        }
        this.numberTimes = Long.parseLong(sb.toString());
    }

    public static NumberDate newNumberDate(long numberTimes) {
        return new NumberDate(numberTimes);
    }

    public static NumberDate newNumberDate(String strDate, String format) {
        Preconditions.checkNotNull(strDate);
        if (!org.apache.commons.lang3.StringUtils.equalsAny(format, SUPPORT_FORMAT_ARRAY)) {
            throw new IllegalArgumentException("不支持的日期格式:" + format);
        }
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException("无法转换的日期:" + strDate);
        }
        return newNumberDate(date);
    }

    public static NumberDate newNumberDate(Date date) {
        Preconditions.checkNotNull(date);
        return new NumberDate(DateUtils.format2Long(date, false));
    }

    public static NumberDate newNumberDateHasMillisecond(Date date) {
        Preconditions.checkNotNull(date);
        return new NumberDate(DateUtils.format2Long(date, true));
    }

    public String format(String format) {
        if (numberTimes <= 0) {
            return "0";
        }
        return DateUtils.format(toDate(), format);
    }

    /**
     * 比较是否在另一个NumberDate之前
     *
     * @param another   - another
     * @param transType - 按照指定类型比较
     * @return
     */
    public boolean before(NumberDate another, TransType transType) {
        Preconditions.checkNotNull(another);

        int n = 0;
        if (TransType.YEAR == transType) {
            n = 4;
        } else if (TransType.MONTH == transType) {
            n = 6;
        } else if (TransType.DAY == transType) {
            n = 8;
        } else if (TransType.HOUR == transType) {
            n = 10;
        } else if (TransType.MINUTE == transType) {
            n = 12;
        } else if (TransType.SECOND == transType) {
            n = 14;
        } else if (TransType.MILLISECOND == transType) {
            n = 17;
        }
        long me = Long.parseLong(String.valueOf(this.numberTimes).substring(0, n));
        long her = Long.parseLong(String.valueOf(another.getNumberTimes()).substring(0, n));
        return me < her;
    }

    /**
     * 比较是否在另一个NumberDate之后
     *
     * @param another   - another
     * @param transType - 按照指定类型比较
     * @return
     */
    public boolean after(NumberDate another, TransType transType) {
        Preconditions.checkNotNull(another);

        int n = 0;
        if (TransType.YEAR == transType) {
            n = 4;
        } else if (TransType.MONTH == transType) {
            n = 6;
        } else if (TransType.DAY == transType) {
            n = 8;
        } else if (TransType.HOUR == transType) {
            n = 10;
        } else if (TransType.MINUTE == transType) {
            n = 12;
        } else if (TransType.SECOND == transType) {
            n = 14;
        } else if (TransType.MILLISECOND == transType) {
            n = 17;
        }
        long me = Long.parseLong(String.valueOf(this.numberTimes).substring(0, n));
        long her = Long.parseLong(String.valueOf(another.getNumberTimes()).substring(0, n));
        return me > her;
    }

    /**
     * 根据指定类型做时间减法
     *
     * @param n         - 要减的时间数
     * @param transType - 按照指定类型
     * @return
     */
    public NumberDate minus(int n, TransType transType) {
        Preconditions.checkArgument(n > 0, "参数n必须大于0");
        return addOrMinus(-n, transType);
    }

    /**
     * 根据指定类型做时间加法
     *
     * @param n         - 要减的时间数
     * @param transType - 按照指定类型
     * @return
     */
    public NumberDate add(int n, TransType transType) {
        Preconditions.checkArgument(n > 0, "参数n必须大于0");
        return addOrMinus(n, transType);
    }

    /**
     * 计算相差时间
     *
     * @param anthor
     * @param transType
     * @return
     */
    public long diff(NumberDate anthor, TransType transType) {
        long timestampMe = this.toDate().getTime();
        long timestampHim = anthor.toDate().getTime();

        long result = -1;
        if (TransType.YEAR == transType) {
            throw new RuntimeException("不支持TransType.YEAR的时间差值计算");
        } else if (TransType.MONTH == transType) {
            throw new RuntimeException("不支持TransType.MONTH的时间差值计算");
        } else if (TransType.DAY == transType) {
            result = Math.abs((timestampMe - timestampHim) / DAY);
        } else if (TransType.HOUR == transType) {
            result = Math.abs((timestampMe - timestampHim) / HOUR);
        } else if (TransType.MINUTE == transType) {
            result = Math.abs((timestampMe - timestampHim) / MINUTE);
        } else if (TransType.SECOND == transType) {
            result = Math.abs((timestampMe - timestampHim) / SECOND);
        } else if (TransType.MILLISECOND == transType) {
            result = Math.abs((timestampMe - timestampHim));
        }
        return result;
    }

    private NumberDate addOrMinus(int n, TransType transType) {
        Calendar me = Calendar.getInstance();
        me.setTime(this.toDate());

        if (TransType.YEAR == transType) {
            me.add(Calendar.YEAR, n);
        } else if (TransType.MONTH == transType) {
            me.add(Calendar.MONTH, n);
        } else if (TransType.DAY == transType) {
            me.add(Calendar.DATE, n);
        } else if (TransType.HOUR == transType) {
            me.add(Calendar.HOUR, n);
        } else if (TransType.MINUTE == transType) {
            me.add(Calendar.MINUTE, n);
        } else if (TransType.SECOND == transType) {
            me.add(Calendar.SECOND, n);
        } else if (TransType.MILLISECOND == transType) {
            throw new RuntimeException("不支持TransType.MILLISECOND的时间增减");
        }

        //原来的位数
        String oldNumberTimesStr = this.toString();
        //加减后都是到秒
        String newNumberTimesStr = NumberDate.newNumberDate(me.getTime()).toString();
        if (newNumberTimesStr.length() > oldNumberTimesStr.length()) {
            //还原原来的位数
            newNumberTimesStr = newNumberTimesStr.substring(0, oldNumberTimesStr.length());
        }
        return NumberDate.newNumberDate(Long.parseLong(newNumberTimesStr));
    }

    /**
     * 转换为java.util.Date类型
     *
     * @return
     */
    public Date toDate() {
        String str = String.valueOf(this.numberTimes);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        if (str.length() >= 4) {
            calendar.set(Calendar.YEAR, Integer.parseInt(str.substring(0, 4)));
        }
        if (str.length() >= 6) {
            calendar.set(Calendar.MONTH, Integer.parseInt(str.substring(4, 6)) - 1);
        }
        if (str.length() >= 8) {
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(str.substring(6, 8)));
        }
        if (str.length() >= 14) {
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(str.substring(8, 10)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(str.substring(10, 12)));
            calendar.set(Calendar.SECOND, Integer.parseInt(str.substring(12, 14)));
        }
        if (str.length() == 17) {
            calendar.set(Calendar.MILLISECOND, Integer.parseInt(str.substring(14, 17)));
        }
        return calendar.getTime();
    }

    @Override
    public String toString() {
        return String.valueOf(this.numberTimes);
    }

    public String toString(TransType transType) {
        if (TransType.YEAR == transType) {
            return String.valueOf(this.numberTimes).substring(0, 4);
        } else if (TransType.MONTH == transType) {
            return String.valueOf(this.numberTimes).substring(0, 6);
        } else if (TransType.DAY == transType) {
            return String.valueOf(this.numberTimes).substring(0, 8);
        } else if (TransType.HOUR == transType) {
            return String.valueOf(this.numberTimes).substring(0, 10);
        } else if (TransType.MINUTE == transType) {
            return String.valueOf(this.numberTimes).substring(0, 12);
        } else if (TransType.SECOND == transType) {
            return String.valueOf(this.numberTimes).substring(0, 14);
        } else if (TransType.MILLISECOND == transType) {
            String str = String.valueOf(this.numberTimes);
            if (str.length() == 17) {
                return String.valueOf(this.numberTimes).substring(0, 17);
            } else {
                return String.valueOf(this.numberTimes) + "000";
            }
        }
        return String.valueOf(this.numberTimes);
    }

    public String toFormatString(String format) {
        if (org.apache.commons.lang3.StringUtils.equalsAny(format, SUPPORT_FORMAT_ARRAY)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(this.toDate());
        }
        throw new IllegalArgumentException("不支持的日期格式:" + format);
    }
}
