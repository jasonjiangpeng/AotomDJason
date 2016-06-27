package zxc.com.gkdvr.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.Parser.ResultParser;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.activitys.SettingDeviceActivity;
import zxc.com.gkdvr.activitys.SettingPhotoActivity;
import zxc.com.gkdvr.activitys.SettingRecordActivity;
import zxc.com.gkdvr.utils.Constance;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.Net.NetCallBack;
import zxc.com.gkdvr.utils.Net.NetParamas;
import zxc.com.gkdvr.utils.Net.NetUtil;
import zxc.com.gkdvr.utils.Tool;


/**
 * Created by dk on 2016/5/27.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    private NetParamas paramas;
    private String result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //view.findViewById(R.id.sdcard).setOnClickListener(this);
        view.findViewById(R.id.recording_setting).setOnClickListener(this);
        view.findViewById(R.id.tack_photo).setOnClickListener(this);
        view.findViewById(R.id.device).setOnClickListener(this);
        //view.findViewById(R.id.about).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.recording_setting:
                intent = new Intent(getActivity(), SettingRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.tack_photo:
                intent = new Intent(getActivity(), SettingPhotoActivity.class);
                startActivity(intent);
                break;
            case R.id.device:
                intent = new Intent(getActivity(), SettingDeviceActivity.class);
                startActivity(intent);
                break;
        }

    }


    private void getVersion() {
        paramas = new NetParamas();
        paramas.put("type", "system");
        paramas.put("action", "getversion");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                MyApplication.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                Tool.showToast(getString(R.string.version) + ResultParser.parseVersion(result));
                            } else {
                                Tool.showToast(getString(R.string.taking_photo_fail) + s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            Tool.removeProgressDialog();
                        }
                    }
                });
            }
        }, getString(R.string.loading), true);
    }


}
