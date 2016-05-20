package com.maple.recordwav;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button start;
    private Button stop;


    ExtAudioRecorder extAudioRecorder = null;
    MiShi miShi;
    String dataPath = "/201/source11.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String ROOT = "";// /storage/emulated/0
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ROOT = Environment.getExternalStorageDirectory().getPath();
            Log.e("app", "系统方法：" + ROOT);
        }
        dataPath = ROOT + dataPath;
        File f = new File(dataPath);
        if (!f.exists())
            f.mkdirs();


        start = (Button) findViewById(R.id.startRecord);
        stop = (Button) findViewById(R.id.stopRecord);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startRecord:
                if (extAudioRecorder == null)
                    extAudioRecorder = extAudioRecorder.getInstance(dataPath);
                extAudioRecorder.prepare();
                extAudioRecorder.start();


//                if (miShi == null)
//                    miShi = new MiShi();
//                miShi.startRecording();
                break;
            case R.id.stopRecord:
                extAudioRecorder.stop();
                extAudioRecorder.release();
                extAudioRecorder = null;

//                miShi.stopRecording();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (extAudioRecorder != null) {
            extAudioRecorder.release();
            extAudioRecorder = null;
        }
    }
}