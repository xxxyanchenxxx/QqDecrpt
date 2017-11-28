package com.qq.decrypt.lib;

import java.lang.reflect.Field;

/**
 * Created by yanchen on 17-11-28.
 */

public class StringUtils {
    static volatile boolean reflactCharArrayResult = true;
    static volatile boolean reflactDataResult = true;
    static Field sCountField = null;
    static Field sValueField = null;

    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_VALUE = "value";

    public static String newStringWithData(char[] data) {
        try {
            String s = new String();
            if (reflactDataResult) {
                if (sValueField == null) {
                    sValueField = String.class.getDeclaredField(COLUMN_VALUE);
                    sValueField.setAccessible(true);
                }
                if (sCountField == null) {
                    sCountField = String.class.getDeclaredField("count");
                    sCountField.setAccessible(true);
                }
                sValueField.set(s, data);
                sCountField.set(s, Integer.valueOf(data.length));
                return s;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
        reflactDataResult = false;
        return new String(data);
    }

    public static char[] getStringValue(StringBuilder sb) {
        try {
            if (sValueField == null) {
                sValueField = StringBuilder.class.getSuperclass().getDeclaredField(COLUMN_VALUE);
                sValueField.setAccessible(true);
            }
            return (char[]) sValueField.get(sb);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return null;
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return null;
        }
    }

    public static char[] reflactCharArray(String str) {
        try {
            if (reflactCharArrayResult) {
                if (sValueField == null) {
                    sValueField = String.class.getDeclaredField(COLUMN_VALUE);
                    sValueField.setAccessible(true);
                }
                return (char[]) sValueField.get(str);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
        reflactCharArrayResult = false;
        return null;
    }
}
