package zxc.com.gkdvr.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zxc.com.gkdvr.R;
import zxc.com.gkdvr.entity.ImageEntity;
import zxc.com.gkdvr.entity.ListFile;
import zxc.com.gkdvr.entity.ListImage;
import zxc.com.gkdvr.fragments.RemoteFileFragment;
import zxc.com.gkdvr.utils.Constance;
import zxc.com.gkdvr.utils.NativeImageLoader;
import zxc.com.gkdvr.utils.UIUtil;

/**
 * Created by dk on 2016/6/6.
 */
public class ImageListAdapter extends BaseAdapter {
    private final DisplayMetrics dm;
    private Context context;
    private final LayoutInflater inflater;
    private LinkedHashMap<String, List<ListImage>> map;
    private List<String> postionKeys;
    private List<ListImage> allListFile = new ArrayList<>();
    private onImageClickListner listner;

    public ImageListAdapter(Context context, LinkedHashMap<String, List<ListImage>> map, onImageClickListner listner) {
        this.map = map;
        this.context = context;
        this.listner = listner;
        inflater = LayoutInflater.from(context);
        dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        postionKeys = new ArrayList<>();
        if (map == null) {
            return;
        }
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            postionKeys.add((String) entry.getKey());
            allListFile.addAll((List<ListImage>) entry.getValue());
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
        List<ListImage> val = map.get(key);
        int count = val.size();
        tvDate.setText(key);
        tvCount.setText(count + context.getString(R.string.image_unit));
        for (int i = 0; i < val.size(); i++) {
            final ImageEntity file = val.get(i).getFile();
            final String path = Constance.BASE_IMAGE_URL + file.getImagename();
            final ImageView img = new ImageView(context);
            int maxWidth = (dm.widthPixels / 3) - UIUtil.dip2px(context, 5);
            int maxHeight = maxWidth;
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView textView = new TextView(context);
            StringBuffer filename = new StringBuffer(file.getImagename().substring(8, 14));
            filename.insert(2, ":");
            filename.insert(5, ":");
            textView.setText(filename.toString());
            textView.setWidth(maxWidth);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(maxWidth, maxHeight);
            img.setLayoutParams(vp);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setCropToPadding(true);
            img.setImageResource(R.mipmap.def_photo_img);
            img.setPadding(UIUtil.dip2px(context, 5), UIUtil.dip2px(context, 5), UIUtil.dip2px(context, 5), UIUtil.dip2px(context, 5));
            Glide.with(context).load(path).centerCrop().into(img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onImageClick(file);
                }
            });
            linearLayout.addView(img);
            linearLayout.addView(textView);
            gridList.addView(linearLayout);
        }
        return convertView;
    }

    public interface onImageClickListner {
        abstract void onImageClick(ImageEntity entity);
    }

}
