package zxc.com.gkdvr.adapter;

import android.content.Context;
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
            int maxWidth = ((int) UIUtil.width / 3) - UIUtil.dip2px(context, 8);
            int maxHeight = maxWidth;
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView textView = new TextView(context);
            textView.setWidth(maxWidth);
            textView.setGravity(Gravity.CENTER);
            textView.setText(file.getVideoname().substring(0, 14));
            LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(maxWidth, maxHeight);
            img.setLayoutParams(vp);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            img.setPadding(UIUtil.dip2px(context, 5), UIUtil.dip2px(context, 5), UIUtil.dip2px(context, 5), UIUtil.dip2px(context, 5));
            img.setImageDrawable(context.getResources().getDrawable(R.mipmap.def_video_img));
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
        abstract void onVideoClick(VideoEntity entity);
    }
}