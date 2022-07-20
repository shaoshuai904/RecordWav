package com.maple.recorder.recording;

import androidx.annotation.RequiresPermission;

import java.io.File;

/**
 * Pcm格式的音频记录器
 *
 * @author maple
 * @time 2018/4/10.
 */
public class PcmRecorder extends BaseDataRecorder {

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    public PcmRecorder(File file, AudioRecordConfig config, PullTransport pullTransport) throws IllegalArgumentException{
        super(file, config, pullTransport);
    }

}