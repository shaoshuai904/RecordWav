package com.maple.recorder.recording;

import java.io.File;

/**
 * Essential APIs for working with MsRecorder.
 *
 * @author maple
 * @time 2018/4/10.
 */
public class MsRecorder {

    private MsRecorder() {
    }

    public static Recorder pcm(File file, AudioRecordConfig config, PullTransport pullTransport) {
        return new PcmRecorder(file, config, pullTransport);
    }

    public static Recorder wav(File file, AudioRecordConfig config, PullTransport pullTransport) {
        return new WavRecorder(file, config, pullTransport);
    }
}
