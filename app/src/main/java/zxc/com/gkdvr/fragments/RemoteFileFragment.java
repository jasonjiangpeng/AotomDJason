package zxc.com.gkdvr.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.sdk.Build;
import com.xw.repo.refresh.PullListView;
import com.xw.repo.refresh.PullToRefreshLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;
import zxc.com.gkdvr.Parser.ResultParser;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.activitys.PhotoActivity;
import zxc.com.gkdvr.activitys.PlaybackActivity2;
import zxc.com.gkdvr.activitys.RemoteFileActivity;
import zxc.com.gkdvr.adapter.ImageListAdapter;
import zxc.com.gkdvr.adapter.VideoListAdapter;
import zxc.com.gkdvr.entity.ImageEntity;
import zxc.com.gkdvr.entity.ListImage;
import zxc.com.gkdvr.entity.ListVideo;
import zxc.com.gkdvr.entity.VideoEntity;
import zxc.com.gkdvr.utils.Constance;
import zxc.com.gkdvr.utils.DateUtil;
import zxc.com.gkdvr.utils.FileAccessor;
import zxc.com.gkdvr.utils.FileUtil;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.Net.NetCallBack;
import zxc.com.gkdvr.utils.Net.NetParamas;
import zxc.com.gkdvr.utils.Net.NetUtil;
import zxc.com.gkdvr.utils.PermissionUtil;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by dk on 2016/6/6.
 */
public class RemoteFileFragment extends Fragment implements VideoListAdapter.onVideoClickListner,
        ImageListAdapter.onImageClickListner, View.OnClickListener, PullToRefreshLayout.OnRefreshListener {
    public final static int ERREO = 3;
    public final static int PROG = 4;
    public final static int OK = 5;
    private ImageView imageNoData;
    private PullListView listView;
    private PullToRefreshLayout pullToRefreshLayout;
    private int type;
    private String[] search = new String[]{"searchimg", "searchfile", "searchevent"};
    private String[] searchType = new String[]{"image", "file", "file"};
    private ImageListAdapter mImageListAdapter;
    private VideoListAdapter videoListAdapter;
    private boolean isFirstLoad = true;

    Handler han = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ERREO:
                    Tool.showToast(getString(R.string.download_fail));
                    break;
                case PROG:
                    int i = msg.arg1;
                    if (i == 100) {
                        Tool.showToast(getString(R.string.download_over));
                        alertDialog.dismiss();
                        return;
                    }
                    MyLogger.i("pro:" + i);
                    progressBar.setProgress(i);
                    progressTv.setText(i + "%");
                    break;
            }
        }
    };
    private File nowFile;
    private List<ImageEntity> images = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (type == 1) {
            receiver = new MyBroadCastReceiver();
            getActivity().registerReceiver(receiver, new IntentFilter(ACTION_UNLOCK_FILE));
        } else if (type == 2) {
            receiver = new MyBroadCastReceiver();
            getActivity().registerReceiver(receiver, new IntentFilter(ACTION_LOCK_FILE));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_remotelfile, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageNoData = (ImageView) view.findViewById(R.id.image_no_data);
        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.pulltorefreshlayout);
        pullToRefreshLayout.setOnRefreshListener(this);
        pullToRefreshLayout.getRefreshFooterView().setVisibility(View.GONE);
        listView = (PullListView) view.findViewById(R.id.listView);
        listView.setEmptyView(imageNoData);
        initData();
    }

    MyBroadCastReceiver receiver;

    public RemoteFileFragment setType(int type) {
        this.type = type;
        return this;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (type != 0)
            getActivity().unregisterReceiver(receiver);
    }

    private void initData() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", searchType[type]);
        paramas.put("action", search[type]);
        paramas.put("index", (type == 0 ? images.size() : videos.size()) + "");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((RemoteFileActivity) getActivity()).initFinish();
                            if (type == 0) {
                                setImage(result);
                            } else {
                                setVideos(result);
                            }
                            pullToRefreshLayout.getRefreshFooterView().setVisibility(View.VISIBLE);
                            isFirstLoad = false;
                        }
                    });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                listView.setPullUpEnable(false);
            }
        });
    }

    private List<VideoEntity> videos = new ArrayList<>();

    private void setVideos(String result) {
        try {
            JSONObject object = JSON.parseObject(result);
            if (object.getString("result").equals("ok")) {
                List<VideoEntity> list = JSON.parseArray(object.getJSONArray("prevideo").toJSONString(), VideoEntity.class);
                if (list != null && list.size() != 0) {
                    videos.addAll(list);
                    setData();
                }
            } else {
                if (!isFirstLoad)
                    Tool.showToast(getString(R.string.no_more_data));
                if (videos.size() == 0) {
                    pullToRefreshLayout.getRefreshFooterView().setVisibility(View.GONE);
                    listView.setPullUpEnable(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listView.setAdapter(new VideoListAdapter(getActivity(), new LinkedHashMap<String, List<ListVideo>>(), null));
        }
    }

    private void setData() {
        LinkedHashMap<String, List<ListVideo>> map = new LinkedHashMap<>();
        for (int i = 0; i < videos.size(); i++) {
            VideoEntity entity = videos.get(i);
            if (entity.getVideostatus() == (type == 1 ? 0 : 1)) continue;
            String fileday = DateUtil.getStringTime(DateUtil.YYYY_MM_DD, FileUtil.getRemoteFileTime(entity.getVideoname()));
            List<ListVideo> list = map.get(fileday);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(new ListVideo(entity, type));
            map.put(fileday, list);
        }
        videoListAdapter = new VideoListAdapter(getActivity(), map, this);
        listView.setAdapter(videoListAdapter);
    }

    private void setImage(String result) {
        try {
            JSONObject object = JSON.parseObject(result);
            if (object.getString("result").equals("ok")) {
                List<ImageEntity> list = JSON.parseArray(object.getJSONArray("preimage").toJSONString(), ImageEntity.class);
                if (list != null && list.size() != 0) {
                    images.addAll(list);
                    setImageData();
                }
            } else {
                Tool.showToast(getString(R.string.no_more_data));
                if (images.size() == 0) {
                    pullToRefreshLayout.getRefreshFooterView().setVisibility(View.GONE);
                    listView.setPullUpEnable(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listView.setAdapter(new ImageListAdapter(getActivity(), new LinkedHashMap<String, List<ListImage>>(), null));
        }
    }

    private void setImageData() {
        LinkedHashMap<String, List<ListImage>> map = new LinkedHashMap<>();
        for (int i = 0; i < images.size(); i++) {
            ImageEntity entity = images.get(i);
            String fileday = DateUtil.getStringTime(DateUtil.YYYY_MM_DD, FileUtil.getRemoteFileTime(entity.getImagename()));
            List<ListImage> list = map.get(fileday);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(new ListImage(entity, type));
            map.put(fileday, list);
        }
        mImageListAdapter = new ImageListAdapter(getActivity(), map, this);
        listView.setAdapter(mImageListAdapter);
    }

    private VideoEntity videoEntity;
    private ImageEntity imageEntity;

    @Override
    public void onVideoClick(VideoEntity entity) {
        filename = entity.getVideoname();
        videoEntity = entity;
        showListDialog();
    }

    @Override
    public void onImageClick(ImageEntity entity) {
        filename = entity.getImagename();
        imageEntity = entity;
        showListDialog();
    }

    private String filename;
    private AlertDialog alertDialog;

    void showListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_items, null);
        builder.setView(view);
        view.findViewById(R.id.open).setOnClickListener(this);
        TextView tv = (TextView) view.findViewById(R.id.lock);
        tv.setOnClickListener(this);
        if (type == 0) {
            tv.setVisibility(View.GONE);
        } else if (type == 2) {
            tv.setText("解锁");
        }
        view.findViewById(R.id.download).setOnClickListener(this);
        view.findViewById(R.id.delete).setOnClickListener(this);
        if (type == 2) {
            view.findViewById(R.id.delete).setVisibility(View.GONE);
        }
        alertDialog = builder.show();
    }


    @Override
    public void onClick(View v) {
        alertDialog.dismiss();
        switch (v.getId()) {
            case R.id.open:
                openFile();
                break;
            case R.id.lock:
                if (type == 1)
                    lockFile();
                else
                    unlockFile();
                break;
            case R.id.download:
                if (!PermissionUtil.hasPermisson(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Tool.showToast(getString(R.string.permission_denied));
                    return;
                }
                downloadFile();
                break;
            case R.id.delete:
                deleteDialog();
                break;
        }
    }

    private void deleteDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.notice))
                .setMessage(getString(R.string.notice_delete_file) + filename)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFile();
                    }
                })
                .show();
        Tool.changeDialogText(dialog);
    }

    private void openFile() {
        String url;
        Intent intent;
        if (type == 0) {
            url = Constance.BASE_IMAGE_URL + filename;
            intent = new Intent(getActivity(), PhotoActivity.class);
            intent.putExtra("path", url);
        } else if (type == 1) {
            url = Constance.BASE_VIDEO_URL + filename;
            intent = new Intent(getActivity(), PlaybackActivity2.class);
            intent.putExtra("videopath", url);
        } else {
            url = Constance.BASE_EVENT_URL + filename;
            intent = new Intent(getActivity(), PlaybackActivity2.class);
            intent.putExtra("videopath", url);
        }
        startActivity(intent);
    }

    private void downloadFile() {
        File dir;
        if (type == 0) {
            dir = new File(FileAccessor.IMESSAGE_IMAGE);
        } else if (type == 1) {
            dir = new File(FileAccessor.IMESSAGE_VIDEO);
        } else {
            dir = new File(FileAccessor.IMESSAGE_PROTECT);
        }
        if (!dir.exists())
            dir.mkdirs();
        nowFile = new File(dir, filename);
        if (nowFile.exists()) {
            Tool.showToast(getString(R.string.file_exists));
            return;
        }
        String url;
        if (type == 0) {
            url = Constance.BASE_IMAGE_URL + filename;
        } else if (type == 1) {
            url = Constance.BASE_VIDEO_URL + filename;
        } else {
            url = Constance.BASE_EVENT_URL + filename;
        }
        NetUtil.download(url, new NetCallBack() {
            @Override
            public void onResponse(Call call, Response response)   {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog();
                        }
                    });
                    saveFile(response, type);
                } catch (Exception e) {
                    han.obtainMessage(ERREO, e.getLocalizedMessage()).sendToTarget();
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(String result) {

            }
        });
    }

    private boolean isNeedDownload = false;

    public File saveFile(Response response, int type) throws Exception {
        isNeedDownload = true;
        InputStream is = null;
        byte[] buf = new byte[1024];
        int len;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;
            fos = new FileOutputStream(nowFile);
            while ((len = is.read(buf)) != -1 && isNeedDownload && sum < total) {
                sum += len;
                fos.write(buf, 0, len);
                Message message = new Message();
                message.what = PROG;
                message.arg1 = (int) (sum * 100.0f / total);
                if (sum == total) {
                    message.arg1 = 100;
                }
                MyLogger.i("sum:" + sum);
                han.sendMessage(message);
            }
            fos.flush();
            return nowFile;
        } catch (Exception e) {
            e.printStackTrace();
            Tool.showToast(getString(R.string.download_fail));
            Tool.removeProgressDialog();
            nowFile.delete();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void lockFile() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "file");
        paramas.put("action", "lockfile");
        paramas.put("name", filename);
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                videos.remove(videoEntity);
                                setData();
                                Intent intent = new Intent(RemoteFileFragment.ACTION_LOCK_FILE);
                                intent.putExtra("video", videoEntity);
                                getActivity().sendBroadcast(intent);
                                Tool.showToast(getString(R.string.lock_file_success));
                            } else {
                                Tool.showToast(s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, getString(R.string.Submiting), true);
        return;
    }

    private void unlockFile() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "file");
        paramas.put("action", "unlockfile");
        paramas.put("name", filename);
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                videos.remove(videoEntity);
                                setData();
                                Intent intent = new Intent(RemoteFileFragment.ACTION_UNLOCK_FILE);
                                intent.putExtra("video", videoEntity);
                                getActivity().sendBroadcast(intent);
                                Tool.showToast(getString(R.string.unlock_file_success));
                            } else Tool.showToast(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, getString(R.string.Submiting), true);
        return;
    }

    private void deleteFile() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", type == 0 ? "image" : "file");
        paramas.put("action", type == 0 ? "deleteimg" : "deletefile");
        paramas.put("name", type == 0 ? filename : filename.substring(0, filename.length() - 4));
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                if (type == 0) {
                                    images.remove(imageEntity);
                                    setImageData();
                                } else {
                                    videos.remove(videoEntity);
                                    setData();
                                }
                                Tool.showToast(getString(R.string.delete_file_success));
                            } else {
                                Tool.showToast(s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            Tool.removeProgressDialog();
                        }
                    }
                });
            }
        }, getString(R.string.Submiting), true);
        return;
    }

    private ProgressBar progressBar;
    private TextView progressTv;

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_progress, null);
        builder.setView(view);
        progressBar = (ProgressBar) view.findViewById(R.id.upload_progress);
        progressTv = (TextView) view.findViewById(R.id.progress_tv);
        view.findViewById(R.id.title_left).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.title_tv)).setText(filename);
        builder.setCancelable(false);
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isNeedDownload = false;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        nowFile.delete();
                    }
                }, 2000);

            }
        });
        alertDialog = builder.show();
        Tool.changeDialogText(alertDialog);
    }

    private void loadMore(final int index) {
        NetParamas paramas = new NetParamas();
        paramas.put("type", searchType[type]);
        paramas.put("action", search[type]);
        paramas.put("index", index + "");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (index == 0)
                                pullToRefreshLayout.refreshFinish(true);
                            else
                                pullToRefreshLayout.loadMoreFinish(true);
                            Tool.removeProgressDialog();
                            if (type == 0) {
                                if (index == 0) images.clear();
                                setImage(result);
                            } else {
                                if (index == 0) videos.clear();
                                setVideos(result);
                            }
                        }
                    });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pullToRefreshLayout.refreshFinish(true);
                            pullToRefreshLayout.loadMoreFinish(true);
                        }
                    });
            }
        }, getString(R.string.loading), true);

    }


    public static final String ACTION_LOCK_FILE = "ACTION_LOCK_FILE";
    public static final String ACTION_UNLOCK_FILE = "ACTION_UNLOCK_FILE";

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        loadMore(0);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        loadMore((type == 0 ? images.size() : videos.size()));
    }


    class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                VideoEntity entity = (VideoEntity) intent.getSerializableExtra("video");
                if (entity != null) {
                    if (intent.getAction().equals(ACTION_LOCK_FILE)) {
                        entity.setVideostatus(0);
                    } else {
                        entity.setVideostatus(1);
                    }
                    videos.add(entity);
                    Collections.sort(videos, new Comparator<VideoEntity>() {
                        @Override
                        public int compare(VideoEntity lhs, VideoEntity rhs) {
                            long ll = FileUtil.getRemoteFileTime(rhs.getVideoname());
                            long lr = FileUtil.getRemoteFileTime(lhs.getVideoname());
                            if (ll > lr) {
                                return 1;
                            } else if (ll < lr) {
                                return -1;
                            }
                            return 0;
                        }
                    });
                    setData();
                }
            }
            Tool.removeProgressDialog();
        }
    }
}
