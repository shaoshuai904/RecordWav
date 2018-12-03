package com.maple.recorder.player;

import android.media.MediaPlayer;

/**
 * @author maple
 * @time 2018/4/8.
 */
public class PlayUtils {
    PlayStateChangeListener playStateChangeListener;
    MediaPlayer player;

    public PlayUtils() {
    }

    public interface PlayStateChangeListener {
        void onPlayStateChange(boolean isPlay);
    }

    public void setPlayStateChangeListener(PlayStateChangeListener listener) {
        this.playStateChangeListener = listener;
        this.playStateChangeListener.onPlayStateChange(false);
    }

    /**
     * 开始播放
     *
     * @param filePath
     */
    public void startPlaying(String filePath) {
        try {
            player = new MediaPlayer();
            player.setDataSource(filePath);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 装载完毕回调
                    player.start();
                }
            });

            if (playStateChangeListener != null) {
                playStateChangeListener.onPlayStateChange(true);
            }

            // play over call back
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        try {
            if (player != null) {
                player.pause();

                if (playStateChangeListener != null) {
                    playStateChangeListener.onPlayStateChange(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放
     */
    public void stopPlaying() {
        try {
            if (player != null) {
                player.stop();
                player.reset();

                if (playStateChangeListener != null) {
                    playStateChangeListener.onPlayStateChange(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        try {
            return player != null && player.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

}
