package com.qq.decrypt.lib;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.qq.decrypt.MainApp;

/**
 * Created by yanchen on 17-11-28.
 */

public class QqDecryptUtil {
    private static final String TAG = "QqDecryptUtil";
    private static char[] codeKey = new char[]{'\u0000', '\u0001', '\u0000', '\u0001'};
    private static int codeKeyLen;
    private static boolean isPassInit = false;


    private static void genCodeKey() {
        if (isPassInit) {
            return;
        }
        String secKey = "";
        if (secKey == null || secKey.length() < codeKey.length) {
            try {
                secKey = ((TelephonyManager) MainApp.getApp().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                if (secKey == null || secKey.length() < codeKey.length) {
                    secKey = ((WifiManager) MainApp.getApp().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (secKey == null || secKey.length() < codeKey.length) {
                secKey = "361910168";
            }
        }
        codeKey = secKey.toCharArray();
        codeKeyLen = codeKey.length;
        isPassInit = true;
    }

    public static byte[] decryptBytes(byte[] bytes) {
        if (bytes == null) {
            return bytes;
        }
        if (!isPassInit) {
            genCodeKey();
        }
        try {
            int length = bytes.length;
            byte[] bytesDec = new byte[length];
            for (int i = 0; i < length; i++) {
                bytesDec[i] = (byte) (bytes[i] ^ codeKey[i % codeKeyLen]);
            }
            return bytesDec;
        } catch (Throwable e) {
            return bytes;
        }
    }

    public static String decryptBytesToStr(byte[] bytes) {
        try {
            byte [] decBytes = decryptBytes(bytes);
            if(decBytes == null || decBytes.length <= 0){
                return null;
            }
            return new String(decBytes,"utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static String decryptString(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        if (!isPassInit) {
            genCodeKey();
        }
        return decryptString(content, false);
    }

    private static String decryptString(String str, boolean z) {
        char[] cArr = null;
        if (str == null) {
            return null;
        }
        try {
            if (z) {
                cArr = StringUtils.reflactCharArray(str);
            }
            if (cArr == null) {
                cArr = new char[str.length()];
                z = false;
            }
            for (int i = 0; i < str.length(); i++) {
                cArr[i] = (char) (str.charAt(i) ^ codeKey[i % codeKeyLen]);
            }
            if (cArr.length == 0) {
                return QzoneConfig.QZONE_SHOW_BREEZE_DEFAULT_BLACK_LIST;
            }
            if (z) {
                return str;
            }
            return StringUtils.newStringWithData(cArr);
        } catch (Throwable th) {
            return QzoneConfig.QZONE_SHOW_BREEZE_DEFAULT_BLACK_LIST;
        }
    }
}
