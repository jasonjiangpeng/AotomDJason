package zxc.com.gkdvr.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.activitys.MainActivity;
import zxc.com.gkdvr.activitys.PhotoActivity;
import zxc.com.gkdvr.activitys.PlaybackActivity2;
import zxc.com.gkdvr.entity.ListFile;
import zxc.com.gkdvr.utils.AccessTokenKeeper;
import zxc.com.gkdvr.utils.DateUtil;
import zxc.com.gkdvr.utils.FileAccessor;
import zxc.com.gkdvr.utils.FileUtil;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.NativeImageLoader;
import zxc.com.gkdvr.utils.Tool;
import zxc.com.gkdvr.utils.UIUtil;
import zxc.com.gkdvr.utils.WifiAdmin;

/**
 * Created by dk on 2016/6/2.
 */
public class LocalFileFragment extends Fragment implements View.OnClickListener {
    private static final String QQ_APP_ID = "1105522668";
    ImageView imageNoData;
    ListView listView;
    private String type;
    private String suffix;
    private String rootPath;
    private FileListAdapter mFileListAdapter;
    private LinkedHashMap<String, List<ListFile>> fileMap;
    private AlertDialog alertDialog;
    private DisplayMetrics dm;
    private ArrayList<File> allFiles = new ArrayList<>();
    private IWXAPI iwxapi;
    private Tencent mTencent;
    private ShareDialog shareDialog;
    public static final String SINA_APP_KEY = "1351762037";
    public static final String WECHAT_APP_KEY = "wx49a0e4294b5e9f46";
    public static final String REDIRECT_URL = "http://www.zxcapp.com";
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_localfile, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageNoData = (ImageView) view.findViewById(R.id.image_no_data);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setEmptyView(imageNoData);
        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        initData();
    }

    public LocalFileFragment setType(String type) {
        try {
            this.type = type;
            this.rootPath = FileAccessor.APPS_ROOT_DIR + "/" + type;
        } catch (Exception e) {
            e.printStackTrace();
            this.rootPath = FileAccessor.APPS_ROOT_DIR;
        }
        return this;
    }

    private void initData() {
        if (type != null) {
            if (type.equalsIgnoreCase("image")) {
                suffix = ".jpg";
            } else if (type.equalsIgnoreCase("video")) {
                suffix = ".mkv";
            } else if (type.equalsIgnoreCase("protect")) {
                suffix = ".mkv";
            }
        }
        if (rootPath == null) {
            return;
        }
        fileMap = getFileDirectory(new File(rootPath));
        if (fileMap == null || fileMap.size() == 0) {
            imageNoData.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            imageNoData.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        mFileListAdapter = new FileListAdapter(getActivity(), fileMap);
        listView.setAdapter(mFileListAdapter);
    }

    private LinkedHashMap<String, List<ListFile>> getFileDirectory(final File rootFile) {
        LinkedHashMap<String, List<ListFile>> map = new LinkedHashMap<>();
        try {
            List<File> fileListFiles = new ArrayList<>();
            File[] files = rootFile.listFiles();
            if (files == null) {
                return null;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    getFileDirectory(rootFile);
                } else if (file.isFile()) {
                    if (file.getName().endsWith(suffix)) {
                        fileListFiles.add(file);
                    }
                }
            }
            MyLogger.i(fileListFiles.toString());
            Collections.sort(fileListFiles, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    long ll = FileUtil.getRemoteFileTime(rhs);
                    long lr = FileUtil.getRemoteFileTime(lhs);
                    if (ll > lr) {
                        return 1;
                    } else if (ll < lr) {
                        return -1;
                    } else {
                        if (lhs.getName().contains("L") && rhs.getName().contains("R")) return -1;
                        if (lhs.getName().contains("R") && rhs.getName().contains("L")) return 1;
                        if (lhs.getName().contains("F") && rhs.getName().contains("B")) return -1;
                        if (lhs.getName().contains("B") && rhs.getName().contains("F")) return 1;
                    }
                    return 0;
                }
            });
            MyLogger.i(fileListFiles.toString());
            allFiles.addAll(fileListFiles);
            for (int i = 0; i < fileListFiles.size(); i++) {
                File f = fileListFiles.get(i);
                String fileday = DateUtil.getStringTime(DateUtil.YYYY_MM_DD, FileUtil.getRemoteFileTime(f));
                List<ListFile> l = map.get(fileday);
                if (l == null) {
                    l = new ArrayList<>();
                }
                l.add(new ListFile(f, type));
                map.put(fileday, l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open:
                alertDialog.dismiss();
                int type = filePath.endsWith(".jpg") ? 1 : 2;
                Intent intent;
                if (type == 1) {
                    intent = new Intent(getActivity(), PhotoActivity.class);
                    intent.putExtra("file", currentFile);
                    intent.putExtra("files",allFiles);
                } else {
                    intent = new Intent(getActivity(), PlaybackActivity2.class);
                    MyLogger.i(filePath);
                    intent.putExtra("videopath", filePath);
                }
                startActivity(intent);
                break;
            case R.id.delete:
                alertDialog.dismiss();
                deleteFile(new File(filePath));
                break;
            case R.id.share:
                alertDialog.dismiss();
                if (((MainActivity) getActivity()).isWifiConnectedToDVR()) {
                    showConnectingDialog();
                    return;
                }
                if (!Tool.isNetconn(getActivity())) {
                    Tool.showToast(getString(R.string.net_fail));
                    return;
                }
                shareDialog = new ShareDialog(getActivity());
                break;
        }
    }

    private void showConnectingDialog() {
        AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).setTitle(getString(R.string.notice))
                .setMessage(getString(R.string.unable_wifi))
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                        startActivity(intent);
                    }
                }).show();
        Tool.changeDialogText(dialog);
    }

//    private PlatformActionListener platformActionListener = new PlatformActionListener() {
//        @Override
//        public void onComplete(cn.sharesdk.framework.Platform platform, int i, HashMap<String, Object> hashMap) {
//            Tool.showToast(i + "");
//        }
//
//        @Override
//        public void onError(cn.sharesdk.framework.Platform platform, int i, Throwable throwable) {
//            Tool.showToast(i + "");
//        }
//
//        @Override
//        public void onCancel(cn.sharesdk.framework.Platform platform, int i) {
//            Tool.showToast(i + "onCancel");
//        }
//    };


    private void deleteFile(final File file) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.notice))
                .setMessage(getString(R.string.notice_delete_file) + file.getName())
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncTask<Void, Void, Boolean>() {

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                Tool.showProgressDialog(getString(R.string.deleting), false, MyApplication.getCurrentActivity());
                            }

                            @Override
                            protected Boolean doInBackground(Void... params) {
                                return file.delete();
                            }

                            @Override
                            protected void onPostExecute(Boolean result) {
                                Tool.removeProgressDialog();
                                Tool.showToast(result ? getString(R.string.delete_success) : getString(R.string.delete_fail));
                                initData();
                                super.onPostExecute(result);
                            }
                        }.execute();
                    }
                })
                .show();
        Tool.changeDialogText(dialog);
    }

    private File currentFile;

    class FileListAdapter extends BaseAdapter {

        private final LayoutInflater inflater;
        private LinkedHashMap<String, List<ListFile>> map;
        private List<String> postionKeys;
        private List<ListFile> allListFile = new ArrayList<>();

        public FileListAdapter(Context context, LinkedHashMap<String, List<ListFile>> map) {
            this.map = map;
            inflater = LayoutInflater.from(context);
            postionKeys = new ArrayList<>();
            if (map == null) {
                return;
            }
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                postionKeys.add((String) entry.getKey());
                allListFile.addAll((List<ListFile>) entry.getValue());
            }
        }

        @Override
        public int getCount() {
            return map == null ? 0 : map.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.item_all_file, null, false);
            TextView tvCount = (TextView) convertView.findViewById(R.id.tvCount);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            final GridLayout gridList = (GridLayout) convertView.findViewById(R.id.gridList);
            gridList.setTag(position);
            String key = postionKeys.get(position);
            final List<ListFile> val = map.get(key);
            int count = val.size();
            tvDate.setText(key);
            tvCount.setText(count + (suffix.equals(".mkv") ? getString(R.string.video_unit) : getString(R.string.image_unit)));
            for (int i = 0; i < val.size(); i++) {
                final File file = val.get(i).getFile();
                if (file.length() == 0) {
                    continue;
                }
                final String path = file.getAbsolutePath();
                final ImageView img = new ImageView(getActivity());
                img.setTag(path);
                int maxWidth = dm.widthPixels / 3 - UIUtil.dip2px(getActivity(), 5);
                int maxHeight = maxWidth;
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                TextView textView = new TextView(getActivity());
                String name = file.getName();
                StringBuffer filename = new StringBuffer(name.substring(8, 14));
                filename.insert(2, ":");
                filename.insert(5, ":");
                if (name.contains("_L")) {
                    textView.setText("L  " + filename.toString());
                } else if (name.contains("_F")) {
                    textView.setText("F  " + filename.toString());
                } else if (name.contains("_R")) {
                    textView.setText("R  " + filename.toString());
                } else if (name.contains("_B")) {
                    textView.setText("B  " + filename.toString());
                } else {
                    textView.setText(filename.toString());
                }
                textView.setWidth(maxWidth);
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(maxWidth, maxHeight);
                img.setLayoutParams(vp);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                img.setImageResource(suffix.equals(".mkv") ? R.mipmap.default_video : R.mipmap.def_photo_img);
                img.setCropToPadding(true);
                img.setPadding(UIUtil.dip2px(getActivity(), 5), UIUtil.dip2px(getActivity(), 5), UIUtil.dip2px(getActivity(), 5), UIUtil.dip2px(getActivity(), 5));
                Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, new Point(maxWidth, maxHeight), new NativeImageLoader.NativeImageCallBack() {
                    @Override
                    public void onImageLoader(Bitmap bitmap, String path) {
                        ImageView mImageView = (ImageView) gridList.findViewWithTag(path);
                        if (bitmap != null && mImageView != null) {
                            mImageView.setImageBitmap(bitmap);
                        }
                    }
                });
                if (bitmap != null) {
                    ImageView mImageView = (ImageView) gridList.findViewWithTag(path);
                    if (mImageView != null) {
                        mImageView.setImageBitmap(bitmap);
                    } else {
                        img.setImageBitmap(bitmap);
                    }
                }
//                if (type.equalsIgnoreCase("video")) {
//                    Tool.getVideoThumbnail(path, 96, 96, MediaStore.Images.Thumbnails.MICRO_KIND, new Tool.onVideoThumbnailLoadedListner() {
//                        @Override
//                        public void onVideoThumbnailLoaded(Bitmap bitmap) {
//                            if (bitmap != null) {
//                                img.setImageBitmap(bitmap);
//                            }
//                        }
//                    });
//                }
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentFile = file;
                        filePath = path;
                        showListDialog();
                    }
                });
                linearLayout.addView(img);
                linearLayout.addView(textView);
                gridList.addView(linearLayout);
            }
            return convertView;
        }
    }


    private String filePath;

    void showListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_items, null);
        builder.setView(view);
        view.findViewById(R.id.open).setOnClickListener(this);
        view.findViewById(R.id.lock).setVisibility(View.GONE);
        view.findViewById(R.id.download).setVisibility(View.GONE);
        if (type.equals("image"))
            view.findViewById(R.id.share).setVisibility(View.VISIBLE);
        view.findViewById(R.id.delete).setOnClickListener(this);
        view.findViewById(R.id.share).setOnClickListener(this);
        alertDialog = builder.show();
    }


    class ShareDialog implements View.OnClickListener {
        private AlertDialog dialog;

        public ShareDialog(Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LinearLayout linearLayout = (LinearLayout) View.inflate(context, R.layout.share_dialog1, null);
            linearLayout.findViewById(R.id.sina).setOnClickListener(this);
            linearLayout.findViewById(R.id.wechat).setOnClickListener(this);
            linearLayout.findViewById(R.id.qq).setOnClickListener(this);
            linearLayout.findViewById(R.id.q_zone).setOnClickListener(this);
            linearLayout.findViewById(R.id.wechat_moments).setOnClickListener(this);
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.setView(linearLayout);
            dialog = builder.show();
            Tool.changeDialogText(dialog);
        }

        /**
         * 关闭对话框
         */
        public void dismiss() {
            if (dialog != null)
                dialog.dismiss();
        }

        @Override
        public void onClick(View v) {
            dismiss();
            switch (v.getId()) {
                case R.id.sina:
                    MainActivity.mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(getActivity(), SINA_APP_KEY);
                    MainActivity.mWeiboShareAPI.registerApp();
                    sendMultiMessage();
                    break;
                case R.id.wechat_moments:
                    shareToWechat(SendMessageToWX.Req.WXSceneTimeline);
                    break;
                case R.id.q_zone:
                    mTencent = Tencent.createInstance(QQ_APP_ID, getActivity());
                    Bundle params = new Bundle();
                    params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
                    ArrayList<String> arrays = new ArrayList<>();
                    arrays.add(filePath);
                    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrays);
                    mTencent.publishToQzone(getActivity(), params, new IUiListener() {
                        @Override
                        public void onComplete(Object o) {
                            Tool.showToast(getString(R.string.weibosdk_demo_toast_share_success));
                        }

                        @Override
                        public void onError(UiError uiError) {
                            Tool.showToast(uiError.errorMessage);
                        }

                        @Override
                        public void onCancel() {
                            Tool.showToast(getString(R.string.weibosdk_demo_toast_share_canceled));
                        }
                    });
                    break;
                case R.id.qq:
                    mTencent = Tencent.createInstance(QQ_APP_ID, getActivity());
                    Bundle params1 = new Bundle();
                    params1.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, filePath);
                    params1.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));
                    params1.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                    mTencent.shareToQQ(getActivity(), params1, new IUiListener() {
                        @Override
                        public void onComplete(Object o) {
                            Tool.showToast(getString(R.string.weibosdk_demo_toast_share_success));
                        }

                        @Override
                        public void onError(UiError uiError) {
                            Tool.showToast(uiError.errorMessage);
                        }

                        @Override
                        public void onCancel() {
                            Tool.showToast(getString(R.string.weibosdk_demo_toast_share_canceled));
                        }
                    });
                    break;
                case R.id.wechat:
                    shareToWechat(SendMessageToWX.Req.WXSceneSession);
                    break;
            }
        }

        private void shareToWechat(int type) {
            iwxapi = WXAPIFactory.createWXAPI(getActivity(), WECHAT_APP_KEY, true);
            iwxapi.registerApp(WECHAT_APP_KEY);
            if (!iwxapi.isWXAppInstalled()) {
                Tool.showToast(getString(R.string.no_wechat_find));
                return;
            }
            if (!iwxapi.isWXAppSupportAPI()) {
                Tool.showToast(getString(R.string.update_wechat));
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            WXImageObject wxImageObject = new WXImageObject(bitmap);
            WXMediaMessage message = new WXMediaMessage();
            message.mediaObject = wxImageObject;
            Bitmap thumb = Bitmap.createScaledBitmap(bitmap, 64, 64, true);
            bitmap.recycle();
            message.thumbData = Bitmap2Bytes(thumb);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("img");
            req.message = message;
            req.scene = type;
            iwxapi.sendReq(req);
            iwxapi.unregisterApp();
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private void sendMultiMessage() {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        ImageObject imageObject = new ImageObject();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        imageObject.setImageObject(bitmap);
        weiboMessage.imageObject = imageObject;
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        AuthInfo authInfo = new AuthInfo(getActivity(), SINA_APP_KEY, REDIRECT_URL, SCOPE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getActivity().getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        MainActivity.mWeiboShareAPI.sendRequest(getActivity(), request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException arg0) {
            }

            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(getActivity().getApplicationContext(), newToken);
            }

            @Override
            public void onCancel() {
            }
        });
    }
}
