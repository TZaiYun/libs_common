package http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务器返回数据的解析工具；
 * 支持XML,json对象,json数组
 * </p>
 *
 * @author 张华洋 2017/1/9 16:00
 * @version V1.2.0
 * @name DataParseUtil
 */


public class DataParseUtil {

    private DataParseUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 解析json对象
     *
     * @param string 要解析的json
     * @param clazz  解析类
     */
    public static <T> T parseObject(String string, Class<T> clazz) {
        return new Gson().fromJson(string, clazz);
    }

    /**
     * 解析json数组为ArrayList
     *
     * @param json  要解析的json
     * @param clazz 解析类
     * @return ArrayList
     */
    public static <T> ArrayList<T> parseToArrayList(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);
        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }


    /**
     * 解析json数组为List
     *
     * @param json  要解析的json
     * @param clazz 解析类
     * @return List
     */
    public static <T> List<T> parseToList(String json, Class<T[]> clazz) {
        Gson gson = new Gson();
        T[] array = gson.fromJson(json, clazz);
        return Arrays.asList(array);
    }


    /**
     * 解析Xml格式数据
     *
     * @param json  要解析的json
     * @param clazz 解析类
     */
    public static Object parseXml(String json, Class<?> clazz) {
        try {
            if (!TextUtils.isEmpty(json) && clazz != null) {
                Serializer serializer = new Serializer() {
                    @Override
                    public <T> T read(Class<? extends T> type, String source) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(Class<? extends T> type, File source) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(Class<? extends T> type, InputStream source) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(Class<? extends T> type, Reader source) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(Class<? extends T> type, InputNode source) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(Class<? extends T> type, String source, boolean strict) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(Class<? extends T> type, File source, boolean strict) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(Class<? extends T> type, InputStream source, boolean strict) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(Class<? extends T> type, Reader source, boolean strict) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(Class<? extends T> type, InputNode source, boolean strict) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(T value, String source) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(T value, File source) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(T value, InputStream source) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(T value, Reader source) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(T value, InputNode source) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(T value, String source, boolean strict) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(T value, File source, boolean strict) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(T value, InputStream source, boolean strict) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(T value, Reader source, boolean strict) throws Exception {
                        return null;
                    }

                    @Override
                    public <T> T read(T value, InputNode source, boolean strict) throws Exception {
                        return null;
                    }

                    @Override
                    public boolean validate(Class type, String source) throws Exception {
                        return false;
                    }

                    @Override
                    public boolean validate(Class type, File source) throws Exception {
                        return false;
                    }

                    @Override
                    public boolean validate(Class type, InputStream source) throws Exception {
                        return false;
                    }

                    @Override
                    public boolean validate(Class type, Reader source) throws Exception {
                        return false;
                    }

                    @Override
                    public boolean validate(Class type, InputNode source) throws Exception {
                        return false;
                    }

                    @Override
                    public boolean validate(Class type, String source, boolean strict) throws Exception {
                        return false;
                    }

                    @Override
                    public boolean validate(Class type, File source, boolean strict) throws Exception {
                        return false;
                    }

                    @Override
                    public boolean validate(Class type, InputStream source, boolean strict) throws Exception {
                        return false;
                    }

                    @Override
                    public boolean validate(Class type, Reader source, boolean strict) throws Exception {
                        return false;
                    }

                    @Override
                    public boolean validate(Class type, InputNode source, boolean strict) throws Exception {
                        return false;
                    }

                    @Override
                    public void write(Object source, File out) throws Exception {

                    }

                    @Override
                    public void write(Object source, OutputStream out) throws Exception {

                    }

                    @Override
                    public void write(Object source, Writer out) throws Exception {

                    }

                    @Override
                    public void write(Object source, OutputNode root) throws Exception {

                    }
                };
                InputStreamReader is = new InputStreamReader(new ByteArrayInputStream(json.getBytes("UTF-8")), "utf-8");
                return serializer.read(clazz, is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
