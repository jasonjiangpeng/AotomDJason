package zxc.com.gkdvr.utils.Net;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.SocketException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by dk on 2016/6/1.
 */
public abstract class NetCallBack implements Callback {
    Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public void onFailure(Call call, IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Tool.removeProgressDialog();
                Tool.showToast(MyApplication.getCurrentActivity().getString(R.string.net_fail));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response){
        try {
            onResponse(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Tool.removeProgressDialog();
                    Tool.showToast(MyApplication.getCurrentActivity().getString(R.string.net_fail));
                }
            });
        }
    }

    public abstract void onResponse(String result);

}
