package data.source;


import data.CommonDataSource;
import http.DataType;
import http.HttpClient;
import http.InfoCallback;
import http.OnResultListener;

/**
 * @author cathy
 * 创建日期 2020/10/13
 * 描述 登录
 */
public class RemoteCommonDataSource implements CommonDataSource {


    @Override
    public void getVersionInfo(String baseUrl, String url, InfoCallback<String> callback) {
        HttpClient client = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .url(url)
                .bodyType(DataType.STRING, String.class)
                .build();

        client.get(new OnResultListener<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(int code, String message) {
                callback.onError(code, message);
            }

            @Override
            public void onFailure(String message) {
                callback.onError(0, message);
            }
        });
    }
}
