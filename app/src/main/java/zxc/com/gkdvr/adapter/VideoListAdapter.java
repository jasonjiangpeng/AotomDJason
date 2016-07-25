package zxc.com.gkdvr.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zxc.com.gkdvr.R;
import zxc.com.gkdvr.entity.ImageEntity;
import zxc.com.gkdvr.entity.ListImage;
import zxc.com.gkdvr.entity.ListVideo;
import zxc.com.gkdvr.entity.VideoEntity;
import zxc.com.gkdvr.utils.Constance;
import zxc.com.gkdvr.utils.UIUtil;

/**
 * Created by dk on 2016/6/6.
 */
public class VideoListAdapter extends BaseAdapter {
    private final DisplayMetrics dm;
    private Context context;
    private final LayoutInflater inflater;
    private LinkedHashMap<String, List<ListVideo>> map;
    private List<String> postionKeys;
    private List<ListVideo> allListFile = new ArrayList<>();
    private onVideoClickListner listner;

    public VideoListAdapter(Context context, LinkedHashMap<String, List<ListVideo>> map, onVideoClickListner listner) {
        this.map = map;
        this.context = context;
        this.listner = listner;
        dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        inflater = LayoutInflater.from(context);
        postionKeys = new ArrayList<>();
        if (map == null) {
            return;
        }
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            postionKeys.add((String) entry.getKey());
            allListFile.addAll((List<ListVideo>) entry.getValue());
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
        GridLayout gridList = (GridLayout) convertView.findViewById(R.id.gridList);
        String key = postionKeys.get(position);
        List<ListVideo> val = map.get(key);
        int count = val.size();
        tvDate.setText(key);
        tvCount.setText(count + context.getString(R.string.video_unit));
        for (int i = 0; i < val.size(); i++) {
            final VideoEntity file = val.get(i).getFile();
            final ImageView img = new ImageView(context);
            int maxWidth = (dm.widthPixels / 3) - UIUtil.dip2px(context, 5);
            int maxHeight = maxWidth;
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            TextView textView = new TextView(context);
            textView.setWidth(maxWidth);
            textView.setGravity(Gravity.CENTER);
            String videoname = file.getVideoname();
            StringBuffer filename = new StringBuffer(videoname.substring(8, 14));
            filename.insert(2, ":");
            filename.insert(5, ":");
            if(videoname.contains("_L")){
                textView.setText("L  "+filename.toString());
            }else if(videoname.contains("_F")){
                textView.setText("F  "+filename.toString());
            }else if(videoname.contains("_R")){
                textView.setText("R  "+filename.toString());
            }else if(videoname.contains("_B")){
                textView.setText("B  "+filename.toString());
            }else {
                textView.setText(filename.toString());
            }
            LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(maxWidth, maxHeight);
            img.setLayoutParams(vp);
            img.setPadding(UIUtil.dip2px(context, 5), UIUtil.dip2px(context, 5), UIUtil.dip2px(context, 5), UIUtil.dip2px(context, 5));
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setCropToPadding(true);
            img.setImageResource(R.mipmap.default_video);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onVideoClick(file);
                }
            });
            linearLayout.addView(img);
            linearLayout.addView(textView);
            gridList.addView(linearLayout);
        }
        return convertView;
    }


    public interface onVideoClickListner {
        void onVideoClick(VideoEntity entity);
    }
}
