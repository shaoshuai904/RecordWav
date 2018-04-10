package com.maple.recorder;

import java.io.File;

/**
 * Essential APIs for working with OmRecorder.
 *
 * @author Kailash Dabhi
 * @date 31-07-2016
 */
public final class OmRecorder {

    private OmRecorder() {
    }

    public static Recorder pcm(File file, AudioRecordConfig config, PullTransport pullTransport) {
        return new Pcm(file, config, pullTransport);
    }

    public static Recorder wav(File file, AudioRecordConfig config, PullTransport pullTransport) {
        return new Wav(file, config, pullTransport);
    }
}
