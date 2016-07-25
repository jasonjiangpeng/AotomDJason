package zxc.com.gkdvr.utils.Net;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by dk on 2016/5/31.
 */
public class NetUtil {
    public static void get(String url, NetParamas params, NetCallBack callback) {
        StringBuilder builder = new StringBuilder(url.trim());
        HashMap<String, String> param = params.getParams();
        for (String key : param.keySet()) {
            builder.append(key);
            if (!key.equals(""))
                builder.append("=");
            builder.append(param.get(key)).append("&");
        }
        builder.deleteCharAt(builder.length() - 1);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(builder.toString()).build();
        client.newCall(request).enqueue(callback);
    }

    public static void get(String url, NetParamas params, NetCallBack callback,boolean del) {
        StringBuilder builder = new StringBuilder(url.trim());
        HashMap<String, String> param = params.getParams();
        for (String key : param.keySet()) {
            builder.append(key);
            if (!key.equals(""))
                builder.append("=");
            builder.append(param.get(key)).append("&");
        }
        if(del)
        builder.deleteCharAt(builder.length() - 1);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(builder.toString()).build();
        client.newCall(request).enqueue(callback);
    }

    public static void get(String url, NetParamas params, final NetCallBack callback, String msg, boolean delLast) {
        try {
            Tool.showProgressDialog(msg, false, MyApplication.getCurrentActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder(url.trim());
        LinkedHashMap<String, String> param = params.getParams();
        for (String key : param.keySet()) {
            builder.append(key)
                    .append("=").append(param.get(key))
                    .append("&");
        }
        if (delLast)
            builder.deleteCharAt(builder.length() - 1);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(builder.toString()).build();
        client.newCall(request).enqueue(callback);
    }

    public static void download(String url, NetCallBack callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }
}
