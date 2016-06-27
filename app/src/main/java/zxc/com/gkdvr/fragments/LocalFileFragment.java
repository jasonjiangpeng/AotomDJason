package zxc.com.gkdvr.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.activitys.PhotoActivity;
import zxc.com.gkdvr.activitys.PlaybackActivity;
import zxc.com.gkdvr.entity.ListFile;
import zxc.com.gkdvr.utils.DateUtil;
import zxc.com.gkdvr.utils.FileAccessor;
import zxc.com.gkdvr.utils.FileUtil;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.NativeImageLoader;
import zxc.com.gkdvr.utils.Tool;
import zxc.com.gkdvr.utils.UIUtil;

/**
 * Created by dk on 2016/6/2.
 */
public class LocalFileFragment extends Fragment implements View.OnClickListener {
    ImageView imageNoData;
    ListView listView;
    private String type;
    private String suffix;
    private String rootPath;
    private FileListAdapter mFileListAdapter;
    private LinkedHashMap<String, List<ListFile>> fileMap;
    private AlertDialog alertDialog;

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
        update();
    }

    private void update() {
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

    private LinkedHashMap<String, List<ListFile>> getFileDirectory(File rootFile) {
        LinkedHashMap<String, List<ListFile>> map = null;
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

            Collections.sort(fileListFiles, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    long ll = FileUtil.getRemoteFileTime(lhs);
                    long lr = FileUtil.getRemoteFileTime(rhs);
                    if (ll > lr) {
                        return 1;
                    } else if (ll < lr) {
                        return -1;
                    }
                    return 0;
                }
            });
            map = new LinkedHashMap<>();
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
                    intent.putExtra("path", filePath);
                } else {
                    intent = new Intent(getActivity(), PlaybackActivity.class);
                    MyLogger.i(filePath);
                    intent.putExtra("videopath", filePath);
                }
                startActivity(intent);
                break;
            case R.id.delete:
                alertDialog.dismiss();
                deleteFile(new File(filePath));
                break;
        }
    }

    private void deleteFile(final File file) {
        new AlertDialog.Builder(getActivity())
                .setTitle("删除警告")
                .setMessage("是否要删除以下文件?\n" + file.getName())
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
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
                                update();
                                super.onPostExecute(result);
                            }
                        }.execute();
                    }
                })
                .show();
    }


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
            tvCount.setText(count + (suffix.equals(".mkv") ? "个" : "张"));
            for (int i = 0; i < val.size(); i++) {
                final File file = val.get(i).getFile();
                if (file.length() == 0) {
                    continue;
                }
                final String path = file.getAbsolutePath();
                final ImageView img = new ImageView(getActivity());
                img.setTag(path);
                int maxWidth = ((int) UIUtil.width / 3) - UIUtil.dip2px(getActivity(), 8);
                int maxHeight = (maxWidth);
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                TextView textView = new TextView(getActivity());
                textView.setText(file.getName().substring(0, 14));
                textView.setWidth(maxWidth);
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(maxWidth, maxHeight);
                img.setLayoutParams(vp);
                img.setImageResource(suffix.equals(".mkv") ? R.mipmap.def_video_img : R.mipmap.def_photo_img);
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
                if (type.equalsIgnoreCase("video")) {
//                    Tool.getVideoThumbnail(path, 96, 96, MediaStore.Images.Thumbnails.MICRO_KIND, new Tool.onVideoThumbnailLoadedListner() {
//                        @Override
//                        public void onVideoThumbnailLoaded(final Bitmap bitmap) {
//
//                            MyApplication.getCurrentActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (bitmap != null)
//                                        img.setImageBitmap(bitmap);
//                                }
//                            });
//                        }
//                    });
                    Bitmap b = Tool.getVideoThumbnail(path);
                    if (b != null) {
                        img.setImageBitmap(b);
                    }
                }
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
        view.findViewById(R.id.delete).setOnClickListener(this);
        alertDialog = builder.show();
    }
}
