package com.maple.recordwav;

import android.app.Application;
import android.util.Log;

import java.io.File;

public class WavApp extends Application {
    private static WavApp app;
    public static File saveFile;

    @Override
    public void onCreate() {
        app = this;
        super.onCreate();

        initFile();
    }

    /**
     * 获取存储路径
     */
    private void initFile() {
        if (saveFile == null) {
            saveFile = getApplicationContext().getExternalFilesDir("wav_file");
        }
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        Log.d("maple_log", "saveFile：" + saveFile.getAbsolutePath());
    }

    public static WavApp app() {
        return app;
    }

}
