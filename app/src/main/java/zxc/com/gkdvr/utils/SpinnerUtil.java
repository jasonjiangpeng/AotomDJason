package zxc.com.gkdvr.utils;

import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import zxc.com.gkdvr.R;

public class SpinnerUtil {

    public static ArrayAdapter makeThemeAndGetAdapterBlack(final Spinner spinner, String[] datas){
        ArrayAdapter<String> modeList = new ArrayAdapter<>(spinner.getContext(), R.layout.simple_spinner_item2, datas);
        modeList.setDropDownViewResource( R.layout.simple_spinner_dropdown_item2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            spinner.post(new Runnable() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    spinner.setDropDownVerticalOffset(spinner.getHeight());
                }
            });
        }
        /**
         * 反射修改Spinner的背景
         */
        try {
            Field f = Spinner.class.getDeclaredField("mPopup");//得到私有属性
            //修改访问权限
            f.setAccessible(true);
            //取得SpinnerPopup(则DropdownPopup)
            Object obj = f.get(spinner);
            //设置下拉布局(DropdownPopup)的背景
            Method method = obj.getClass().getMethod("setBackgroundDrawable", Drawable.class);
            //修改访问权限
            method.setAccessible(true);
            //调用setBackgroundDrawable方法并设置半透明颜色ColorDrawable
            method.invoke(obj, new ColorDrawable(0x88000000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modeList;
    }


    public static ArrayAdapter makeThemeAndGetAdapterWhile(final Spinner spinner,String[] datas){
        ArrayAdapter<String> modeList = new ArrayAdapter<>(spinner.getContext(), R.layout.simple_spinner_item1, datas);
        modeList.setDropDownViewResource( R.layout.simple_spinner_dropdown_item1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            spinner.post(new Runnable() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    spinner.setDropDownVerticalOffset(spinner.getHeight());
                }
            });
        }
        /**
         * 反射修改Spinner的背景
         */
        try {
            Field f = Spinner.class.getDeclaredField("mPopup");//得到私有属性
            //修改访问权限
            f.setAccessible(true);
            //取得SpinnerPopup(则DropdownPopup)
            Object obj = f.get(spinner);
            //设置下拉布局(DropdownPopup)的背景
            Method method = obj.getClass().getMethod("setBackgroundDrawable", Drawable.class);
            //修改访问权限
            method.setAccessible(true);
            //调用setBackgroundDrawable方法并设置半透明颜色ColorDrawable
            method.invoke(obj,spinner.getContext().getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modeList;
    }
}
