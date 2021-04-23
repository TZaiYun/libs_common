package data;

import http.InfoCallback;

public interface CommonDataSource {

    /**
     * 获取线上版本信息
     * @param baseUrl
     * @param url
     * @param callback
     */
    void getVersionInfo( String baseUrl, String url, InfoCallback<String> callback);

}
