package http;


import androidx.annotation.Keep;

/**
 * <p>数据回调接口</p>
 *
 * @version V1.2.0
 * @name InfoCallback
 */
@Keep
public interface InfoCallback<String> {

    void onSuccess(String info);

    void onError(int code, String message);

}
