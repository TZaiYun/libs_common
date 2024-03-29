package http;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.android.tu.loadingdialog.LoadingDailog;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.nisco.common_libs.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import utils.CommonUtils;
import utils.StringUtils;

/**
 * <p>类说明</p>
 *
 * @version V1.0.0
 * @name HttpClient
 */
public class HttpClient {

    /*The certificate's password*/
    private static final String STORE_PASS = "6666666";
    private static final String STORE_ALIAS = "666666";
    /*用户设置的BASE_URL*/
    private static String BASE_URL = "";
    /*本地使用的baseUrl*/
    private String baseUrl = "";
    private static OkHttpClient okHttpClient;
    private Builder mBuilder;
    private Retrofit retrofit;
    private Call<ResponseBody> mCall;
    private static final Map<String, Call> CALL_MAP = new HashMap<>();
    private LoadingDailog dailog;


    /**
     * 获取HttpClient的单例
     *
     * @return HttpClient的唯一对象
     */
    private static HttpClient getIns() {
        return HttpClientHolder.sInstance;
    }

    /**
     * 单例模式中的静态内部类写法
     */
    private static class HttpClientHolder {
        private static final HttpClient sInstance = new HttpClient();
    }

    private HttpClient() {
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(CommonUtils.getContext()));
        //HttpsUtil.SSLParams sslParams = HttpsUtil.getSslSocketFactory(CommonUtils.getContext(), R.raw.cer,STORE_PASS , STORE_ALIAS);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor(null, true))
                .cookieJar(cookieJar)
                .build();
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    private void setBuilder(Builder builder) {
        this.mBuilder = builder;
    }

    /**
     * 获取的Retrofit的实例，
     * 引起Retrofit变化的因素只有静态变量BASE_URL的改变。
     */
    private void getRetrofit() {
        if (!BASE_URL.equals(baseUrl) || retrofit == null) {
            baseUrl = BASE_URL;
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .build();
        }
    }

    public void partyPost(final OnResultListener onResultListener) {
        Builder builder = mBuilder;
        mCall = retrofit.create(ApiService.class)
                .executePost(builder.url, builder.params);
        putCall(builder, mCall);
        request(builder, onResultListener, 0);
    }

    public void post(final OnResultListener onResultListener) {
        Builder builder = mBuilder;
        mCall = retrofit.create(ApiService.class)
                .post(builder.headers, builder.url, builder.params);
        putCall(builder, mCall);
        request(builder, onResultListener, 0);
    }

    public void requestBodyPost(final OnResultListener onResultListener) {
        Builder builder = mBuilder;
        mCall = retrofit.create(ApiService.class)
                .requestBodyPost(builder.headers, builder.url, builder.requestBodyMap);
        putCall(builder, mCall);
        request(builder, onResultListener, 0);
    }

    public void videoPost(Map<String, String> headers, final OnResultListener onResultListener) {
        Builder builder = mBuilder;
        mCall = retrofit.create(ApiService.class)
                .videoPost(headers, builder.url, builder.body);
        putCall(builder, mCall);
        request(builder, onResultListener, 0);
    }
    public void djPost(final OnResultListener onResultListener) {
        Builder builder = mBuilder;
        mCall = retrofit.create(ApiService.class)
                .djPost( builder.url, builder.body);
        putCall(builder, mCall);
        request(builder, onResultListener, 0);
    }
    public void downloadFile(final OnResultListener onResultListener) {
        Builder builder = mBuilder;
        mCall = retrofit.create(ApiService.class)
                .download(builder.url);
        putCall(builder, mCall);
        request(builder, onResultListener, 1);
    }

    public void postImg(final OnResultListener onResultListener) {
        Builder builder = mBuilder;
        mCall = retrofit.create(ApiService.class)
                .upImg(builder.url, builder.parts);
        putCall(builder, mCall);
        request(builder, onResultListener, 0);
    }

    public void uploadFile(final OnResultListener onResultListener) {
        Builder builder = mBuilder;
        mCall = retrofit.create(ApiService.class)
                .uploadFile(builder.headers,builder.url, builder.body);
        putCall(builder, mCall);
        request(builder, onResultListener, 0);
    }

    public void get(final OnResultListener onResultListener) {
        Builder builder = mBuilder;
        if (!builder.params.isEmpty()) {
            String value = "";
            for (Map.Entry<String, String> entry : builder.params.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue();
                String span = value.equals("") ? "" : "&";
                String part = StringUtils.buffer(span, mapKey, "=", mapValue);
                value = StringUtils.buffer(value, part);
            }
            builder.url(StringUtils.buffer(builder.url, "?", value));
        }
        mCall = retrofit.create(ApiService.class).executeGet(builder.url);
        putCall(builder, mCall);
        request(builder, onResultListener, 0);
    }


    private void request(final Builder builder, final OnResultListener onResultListener, final int flag) {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showLong(R.string.current_internet_invalid);
            onResultListener.onFailure(CommonUtils.getString(R.string.current_internet_invalid));
            return;
        }

        mCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                dailog.dismiss();
                if (200 == response.code()) {
                    try {
                        if (flag == 0) {
                            String result = response.body().string();
                            parseData(result, builder.clazz, builder.bodyType, onResultListener);
                        } else if (flag == 1) {
                            parseFileData(response.body(), builder.clazz, builder.bodyType, onResultListener);
                        }

                    } catch (IOException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
                if (!response.isSuccessful() || 200 != response.code()) {
                    onResultListener.onError(response.code(), response.message());
                }
                if (null != builder.tag) {
                    removeCall(builder.url);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                dailog.dismiss();
                t.printStackTrace();
                onResultListener.onFailure(t.getMessage());
                if (null != builder.tag) {
                    removeCall(builder.url);
                }
            }

        });
    }


    /**
     * 添加某个请求
     */
    private synchronized void putCall(Builder builder, Call call) {
        if (builder.tag == null)
            return;
        synchronized (CALL_MAP) {
            CALL_MAP.put(builder.tag.toString() + builder.url, call);
        }

    }


    /**
     * 取消某个界面都所有请求，或者是取消某个tag的所有请求;
     * 如果要取消某个tag单独请求，tag需要传入tag+url
     *
     * @param tag 请求标签
     */
    public synchronized void cancel(Object tag) {
        if (tag == null)
            return;
        List<String> list = new ArrayList<>();
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.startsWith(tag.toString())) {
                    CALL_MAP.get(key).cancel();
                    list.add(key);
                }
            }
        }
        for (String s : list) {
            removeCall(s);
        }

    }

    /**
     * 移除某个请求
     *
     * @param url 添加的url
     */
    private synchronized void removeCall(String url) {
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.contains(url)) {
                    url = key;
                    break;
                }
            }
            CALL_MAP.remove(url);
        }
    }

    /**
     * Build a new HttpClient.
     * url is required before calling. All other methods are optional.
     */
    public static final class Builder {
        private String builderBaseUrl = "";
        private String url;
        private Object tag;
        private Map<String, String> params = new HashMap<>();
        /*返回数据的类型,默认是string类型*/
        @DataType.Type
        private int bodyType = DataType.STRING;
        /*解析类*/
        private Class clazz;
        private RequestBody body;
        private List<MultipartBody.Part> parts;

        private Map<String, RequestBody> requestBodyMap = new HashMap<>();
        //添加请求头
        private Map<String, String> headers = new HashMap<>();
        private MultipartBody.Part part;

        public Builder() {
        }

        /**
         * 请求地址的baseUrl，最后会被赋值给HttpClient的静态变量BASE_URL；
         *
         * @param baseUrl 请求地址的baseUrl
         */
        public Builder baseUrl(String baseUrl) {
            this.builderBaseUrl = baseUrl;
            return this;
        }

        /**
         * 除baseUrl以外的部分，
         * 例如："mobile/login"
         *
         * @param url path路径
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * 给当前网络请求添加标签，用于取消这个网络请求
         *
         * @param tag 标签
         */
        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param key   键
         * @param value 值
         */
        public Builder params(String key, String value) {
            this.params.put(key, value);
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param params
         */
        public Builder params(Map<String, String> params) {
            this.params = params;
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param key   键
         * @param value 值
         */
        public Builder addHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param key
         */
        public Builder addRequestBody(String key, RequestBody requestBody) {
            this.requestBodyMap.put(key, requestBody);
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param requestBodyMap
         */
        public Builder requestBodyMap(Map<String, RequestBody> requestBodyMap) {
            this.requestBodyMap = requestBodyMap;
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param body
         */
        public Builder body(RequestBody body) {
            this.body = body;
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param parts
         */
        public Builder parts(List<MultipartBody.Part> parts) {
            this.parts = parts;
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param part
         */
        public Builder part(MultipartBody.Part part) {
            this.part = part;
            return this;
        }

        /**
         * 响应体类型设置,如果要响应体类型为STRING，请不要使用这个方法
         *
         * @param bodyType 响应体类型，分别:STRING，JSON_OBJECT,JSON_ARRAY,XML
         * @param clazz    指定的解析类
         * @param <T>      解析类
         */
        public <T> Builder bodyType(@DataType.Type int bodyType, @NonNull Class<T> clazz) {
            this.bodyType = bodyType;
            this.clazz = clazz;
            return this;
        }

        public HttpClient build() {
            if (!TextUtils.isEmpty(builderBaseUrl)) {
                BASE_URL = builderBaseUrl;
            }
            HttpClient client = HttpClient.getIns();
            client.getRetrofit();
            client.setBuilder(this);
            return client;
        }
    }

    /**
     * 数据解析方法
     *
     * @param data             要解析的数据
     * @param clazz            解析类
     * @param bodyType         解析数据类型
     * @param onResultListener 回调方数据接口
     */
    @SuppressWarnings("unchecked")
    private void parseData(String data, Class clazz, @DataType.Type int bodyType, OnResultListener onResultListener) {
        switch (bodyType) {
            case DataType.STRING:
                onResultListener.onSuccess(data);
                break;
            case DataType.JSON_OBJECT:
                onResultListener.onSuccess(DataParseUtil.parseObject(data, clazz));
                break;
            case DataType.JSON_ARRAY:
                onResultListener.onSuccess(DataParseUtil.parseToArrayList(data, clazz));
                break;
            case DataType.XML:
                onResultListener.onSuccess(DataParseUtil.parseXml(data, clazz));
                break;
            default:
                break;
        }
    }

    private void parseFileData(ResponseBody data, Class clazz, @DataType.Type int bodyType, OnResultListener onResultListener) {
        switch (bodyType) {
            case DataType.RESPONSE_BODY:
                onResultListener.onSuccess(data);
                break;
        }
    }
}
