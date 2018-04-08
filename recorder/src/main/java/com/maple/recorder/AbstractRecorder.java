package com.maple.recorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kailash Dabhi
 * @date 22-08-2016.
 * Copyright (c) 2017 Kingbull Technology. All rights reserved.
 */
public abstract class AbstractRecorder implements Recorder {
    protected final PullTransport pullTransport;
    protected final File file;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private OutputStream outputStream;
    private final Runnable recordingTask = new Runnable() {
        @Override
        public void run() {
            try {
                pullTransport.start(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (IllegalStateException e) {
                throw new RuntimeException("AudioRecord state has uninitialized state", e);
            }
        }
    };

    protected AbstractRecorder(PullTransport pullTransport, File file) {
        this.pullTransport = pullTransport;
        this.file = file;
    }

    @Override
    public void startRecording() {
        outputStream = outputStream(file);
        executorService.submit(recordingTask);
    }

    private OutputStream outputStream(File file) {
        if (file == null) {
            throw new RuntimeException("file is null !");
        }
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(
                    "could not build OutputStream from" + " this file " + file.getName(), e);
        }
        return outputStream;
    }

    @Override
    public void stopRecording() throws IOException {
        pullTransport.stop();
        outputStream.flush();
        outputStream.close();
    }

    @Override
    public void pauseRecording() {
        pullTransport.pullableSource().isEnableToBePulled(false);
    }

    @Override
    public void resumeRecording() {
        pullTransport.pullableSource().isEnableToBePulled(true);
        executorService.submit(recordingTask);
    }
}
