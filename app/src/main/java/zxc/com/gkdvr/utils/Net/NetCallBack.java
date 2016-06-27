package zxc.com.gkdvr.utils.Net;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by dk on 2016/6/1.
 */
public abstract class NetCallBack implements Callback {
    Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public void onFailure(Call call, IOException e) {
        Tool.removeProgressDialog();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Tool.showToast("网络连接异常");
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        onResponse(response.body().string());
    }

    public abstract void onResponse(String result);

}
