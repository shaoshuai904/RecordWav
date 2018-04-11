package com.maple.recorder.recording;

import java.io.IOException;

/**
 * 一种可以用startRecording()和stopRecording()方法开始和停止记录的记录器。
 *
 * @author maple
 * @time 2018/4/10.
 */
public interface Recorder {

    /**
     * 开始
     */
    void startRecording();

    /**
     * 暂停
     */
    void pauseRecording();

    /**
     * 继续
     */
    void resumeRecording();

    /**
     * 停止
     */
    void stopRecording() throws IOException;


}
