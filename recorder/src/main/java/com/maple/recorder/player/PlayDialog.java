package com.maple.recorder.player;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.maple.recorder.R;

import junit.framework.Assert;

import java.io.File;

/**
 * @author maple
 * @time 2018/4/23.
 */
public class PlayDialog extends Dialog {
    ImageButton ib_play;
    SeekBar sb_bar;
    TextView tv_file_name;
    TextView tv_left_time;
    TextView tv_right_time;

    public PlayDialog(Context context) {
        this(context, false);
    }

    public PlayDialog(Context context, boolean isShowAsFloatWindow) {
        super(context, R.style.CustomDialog);
        if (!isShowAsFloatWindow) {
            Assert.assertTrue("context must be Activity in Dialog.", context instanceof Activity);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        this.setCancelable(true);

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_play_view);

        ib_play = findViewById(R.id.ib_play);
        sb_bar = findViewById(R.id.sb_bar);
        tv_file_name = findViewById(R.id.tv_file_name);
        tv_left_time = findViewById(R.id.tv_left_time);
        tv_right_time = findViewById(R.id.tv_right_time);

        findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
    }

    public PlayDialog addWavFile(File file) {

        tv_left_time.setText("0:00");
        tv_right_time.setText("0:40");

        return this;
    }

    public void showDialog() {
        showDialog(true);
    }

    public void showDialog(boolean cancelable) {
        this.setCancelable(cancelable);
        this.show();
    }

    public void dismissDialog() {
        dismiss();
    }
}
