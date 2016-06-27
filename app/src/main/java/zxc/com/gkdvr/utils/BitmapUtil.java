package zxc.com.gkdvr.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BitmapUtil {

	public static byte[] getBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap smallBitmap(Bitmap bitmap, float scale) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	public static Bitmap getUserBitmap(String name) {
		return BitmapFactory.decodeFile(name);
	}

	public static Bitmap getBitmap(String path) {
		if (!"".equals(path)) {
			return BitmapFactory.decodeFile(path);
		} else {
			return null;
		}
	}

	public static Bitmap getEnglishBitmap(String name) {
		String path = Environment.getExternalStorageDirectory().getPath()
				+ name;
		System.out.println(Environment.getExternalStorageDirectory().getPath()
				+ name);
		return BitmapFactory.decodeFile(path);
	}

	public static void saveImage(Bitmap bm, String name) {
		String path = Environment.getExternalStorageDirectory().getPath()
				+ name;
		if (bm == null)
			return;
		try {
			File file = new File(path);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists())
				file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			bm.compress(CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void save(Bitmap bm, String path) {
		if (bm == null)
			return;
		try {
			File file = new File(path);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists())
				file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			bm.compress(CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Bitmap getScalBitmap(Bitmap bitmap, int width, int height,
			int rotate) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		float scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		matrix.postRotate(rotate);
		System.out.println(w + ":" + h + "---------");
		System.out.println(width + ":" + height);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		bitmap.recycle();
		bitmap = null;
		System.out.println(newbmp.getWidth() + ":" + newbmp.getHeight());
		System.out.println(width + ":" + height);
		matrix = new Matrix();
		Bitmap newbmp2 = Bitmap.createBitmap(newbmp,
				(int) ((newbmp.getWidth() - width) / 2.0),
				(int) ((newbmp.getHeight() - height) / 2.0), width, height,
				matrix, true);
		return newbmp2;
	}

	public static String getSDPath() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String sdDir = Environment.getExternalStorageDirectory().getPath();
			return sdDir;
		}
		return null;
	}

	public static Bitmap getNetWorkBitmap(String urlString) {
		URL imgUrl = null;
		Bitmap bitmap = null;
		try {
			imgUrl = new URL(urlString);
			// 使用HttpURLConnection打开连接
			HttpURLConnection urlConn = (HttpURLConnection) imgUrl
					.openConnection();
			urlConn.setDoInput(true);
			urlConn.connect();
			// 将得到的数据转化成InputStream
			InputStream is = urlConn.getInputStream();
			// 将InputStream转换成Bitmap
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("[getNetWorkBitmap->]MalformedURLException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[getNetWorkBitmap->]IOException");
			e.printStackTrace();
		}
		return bitmap;
	}
	
	 

    /**
     * 根据源bmp 进行宽高缩放
     */
    public static Bitmap getScaleSize(Bitmap source, float scaleW, float scaleH) {
        int w = (int)(source.getWidth() * scaleW);
        int h = (int)(source.getHeight() * scaleH);
        return ThumbnailUtils.extractThumbnail(source, w, h);
    }

    /**
     * 根据源bmp转换到指定的宽高
     */
    public static Bitmap getSolidSize(Bitmap source, int width, int height) {
        return ThumbnailUtils.extractThumbnail(source, width, height);
    }

    /**
     * 根据文件路径取得固定宽高的Bitmap
     */
    public static Bitmap getSolidSizeByPath(String bmpPath, int width, int height) {
        Bitmap bmp = getBitmap(bmpPath);
        if (bmp == null) {
            
            return null;
        }
        Bitmap result = ThumbnailUtils.extractThumbnail(bmp, width, height);
        bmp.recycle();
        return result;
    }

    /**
     * 根据文件取得固定宽高的Bitmap
     */
    public static Bitmap getSolidSizeByFile(File bmpFile, int width, int height) {
        return getSolidSizeByPath(bmpFile.getPath(), width, height);
    }

	
}
