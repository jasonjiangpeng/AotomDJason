package zxc.com.gkdvr.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者: DongZhi 2016/2/2.
 * 保佑以下代码无bug...
 */
public class NativeImageLoader {
    private LruCache<String, Bitmap> mMemoryCache;
    private static NativeImageLoader mInstance = new NativeImageLoader();
    private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(1);


    private NativeImageLoader() {
        //获取应用程序的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        //用最大内存的1/4来存储图片
        final int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            //获取每张图片的大小
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };

    }

    public void evictAll() {

        mMemoryCache.evictAll();

    }


    /**
     * 通过此方法来获取NativeImageLoader的实例
     *
     * @return
     */
    public static NativeImageLoader getInstance() {
        return mInstance;
    }


    /**
     * 加载本地图片，对图片不进行裁剪
     *
     * @param path
     * @param mCallBack
     * @return
     */
    public Bitmap loadNativeImage(final String path, final NativeImageCallBack mCallBack) {
        return this.loadNativeImage(path, null, mCallBack);
    }

    /**
     * 此方法来加载本地图片，这里的mPoint是用来封装ImageView的宽和高，我们会根据ImageView控件的大小来裁剪Bitmap
     * 如果你不想裁剪图片，调用loadNativeImage(final String path, final NativeImageCallBack mCallBack)来加载
     *
     * @param path
     * @param mPoint
     * @param mCallBack
     * @return
     */
    public Bitmap loadNativeImage(final String path, final Point mPoint, final NativeImageCallBack mCallBack) {
        //先获取内存中的Bitmap
        Bitmap bitmap = getBitmapFromMemCache(path);

        final Handler mHander = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mCallBack.onImageLoader((Bitmap) msg.obj, path);
            }

        };

        //若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
        if (bitmap == null) {
            mImageThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    //先获取图片的缩略图
                    Bitmap mBitmap = null;
                    if (path.endsWith(".avi")) {//获取视频缩略图
                        mBitmap = getVideoThumbnail(path, mPoint == null ? 0 : mPoint.x, mPoint == null ? 0 : mPoint.y, MediaStore.Images.Thumbnails.MINI_KIND);
                    } else {//获取图片缩略图
                        mBitmap = BitmapUtil.getSolidSizeByFile(new File(path), mPoint == null ? 0 : mPoint.x, mPoint == null ? 0 : mPoint.y);
                        //decodeThumbBitmapForFile(path, mPoint == null ? 0 : mPoint.x, mPoint == null ? 0 : mPoint.y);
                    }
                    Message msg = mHander.obtainMessage();
                    msg.obj = mBitmap;
                    mHander.sendMessage(msg);
                    //将图片加入到内存缓存
                    addBitmapToMemoryCache(path, mBitmap);
                }
            });
        }
        return bitmap;

    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @param kind      参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *                  其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    /**
     * 往内存缓存中添加Bitmap
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 根据key来获取内存中的图片
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    /**
     * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
     *
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //设置缩放比例
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);

        //设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }


    /**
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
     *
     * @param options
     */
    private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
        int inSampleSize = 1;
        if (viewWidth == 0 || viewWidth == 0) {
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        //假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

            //为了保证图片不缩放变形，我们取宽高比例最小的那个
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }


    /**
     * 加载本地图片的回调接口
     *
     * @author xiaanming
     */
    public interface NativeImageCallBack {
        /**
         * 当子线程加载完了本地的图片，将Bitmap和图片路径回调在此方法中
         *
         * @param bitmap
         * @param path
         */
        void onImageLoader(Bitmap bitmap, String path);
    }
}