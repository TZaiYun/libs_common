package utils;

import com.blankj.utilcode.util.ToastUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 字符串相关工具类
 */
public class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 字符串拼接,线程安全
     */
    public static String buffer(String... array) {
        StringBuffer s = new StringBuffer();
        for (String str : array) {
            s.append(str);
        }
        return s.toString();
    }

    /**
     * 字符串拼接,线程不安全,效率高
     */
    public static String builder(String... array) {
        StringBuilder s = new StringBuilder();
        for (String str : array) {
            s.append(str);
        }
        return s.toString();
    }

    public static String toURLEncoded(String paramString) {
        try {
            String str = URLEncoder.encode(URLEncoder.encode(paramString, "UTF-8"), "UTF-8");
            return str;
        } catch (Exception localException) {
        }

        return "";
    }

    public static String toURLDecode(String paramString) {
        try {
            String str = URLDecoder.decode(URLDecoder.decode(paramString, "UTF-8"), "UTF-8");
            return str;
        } catch (Exception localException) {
        }

        return "";
    }

    public static boolean isValidate(String... values) {
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (value == null || value == "") {
                ToastUtils.showShort("请输入必填字段！");
                return false;
            }
        }
        return true;
    }

    public static boolean hasText(CharSequence str) {
        return (str != null && str.length() > 0 && containsText(str));

    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

}