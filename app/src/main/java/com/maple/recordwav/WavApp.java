package com.maple.recordwav;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class WavApp extends Application {
    private static WavApp app;

    public static String rootPath = "/wav_file/";

    @Override
    public void onCreate() {
        app = this;
        super.onCreate();

        initPath();
    }

    /**
     * 初始化存储路径
     */
    private void initPath() {
        String ROOT = "";// /storage/emulated/0
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            ROOT = getBaseContext().getFilesDir().getPath();
            ROOT = Environment.getExternalStorageDirectory().getPath();
            Log.e("app", "系统方法：" + ROOT);
        }
        rootPath = ROOT + rootPath;

        File lrcFile = new File(rootPath);
        if (!lrcFile.exists()) {
            lrcFile.mkdirs();
        }
    }


    public static WavApp app() {
        return app;
    }


}
