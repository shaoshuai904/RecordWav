package com.maple.recorder.player;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.maple.recorder.R;

import junit.framework.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author maple
 * @time 2018/4/23.
 */
public class PlayDialog extends Dialog {
    ImageButton ib_play;
    ImageView iv_cancel;
    SeekBar sb_bar;
    TextView tv_file_name;
    TextView tv_left_time;
    TextView tv_right_time;

    File file;
    MediaPlayer player;

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
        initListener();
    }

    private void initView() {
        setContentView(R.layout.dialog_play_view);

        ib_play = findViewById(R.id.ib_play);
        iv_cancel = findViewById(R.id.iv_cancel);
        sb_bar = findViewById(R.id.sb_bar);
        tv_file_name = findViewById(R.id.tv_file_name);
        tv_left_time = findViewById(R.id.tv_left_time);
        tv_right_time = findViewById(R.id.tv_right_time);

        tv_left_time.setText("0:00");
        tv_right_time.setText("0:40");
    }

    private void initListener() {
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        ib_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPlay();
            }
        });
        sb_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void clickPlay() {
        if (!isPlaying()) {
            startPlaying();
        } else {
            pausePlay();
        }
    }

    public PlayDialog addWavFile(File file) {
        this.file = file;
        try {
            player = new MediaPlayer();
            player.setDataSource(file.getAbsolutePath());
            player.prepare();
            // play over call back
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });

            sb_bar.setProgress(0);
            sb_bar.setMax(player.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        stopPlaying();
        dismiss();
    }

    //--------------------------------------------------------------------------

    public void startPlaying() {
        if (player != null) {
            player.start();
            ib_play.setBackground(getContext().getDrawable(R.drawable.ic_pause));
            //----------定时器记录播放进度---------//
            TimerTask mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    sb_bar.setProgress(player.getCurrentPosition());
                }
            };
            new Timer().schedule(mTimerTask, 0, 10);
        }
    }

    public void pausePlay() {
        if (player != null) {
            player.pause();
            ib_play.setBackground(getContext().getDrawable(R.drawable.ic_play));
        }
    }

    public void stopPlaying() {
        if (player != null) {
            player.stop();
            player.reset();
            ib_play.setBackground(getContext().getDrawable(R.drawable.ic_play));
        }
    }

    public boolean isPlaying() {
        try {
            return player != null && player.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

}
