package com.maple.recorder;

import java.io.File;

/**
 * {@code Pcm} is recorder for recording audio in wav format.
 *
 * @author Kailash Dabhi
 * @date 31-07-2016
 */
final class Pcm extends AbstractRecorder {

    public Pcm(File file, AudioRecordConfig config, PullTransport pullTransport) {
        super(file, config, pullTransport);
    }

}