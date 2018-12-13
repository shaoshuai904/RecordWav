package com.maple.recorder.recording;

/**
 * 录音机接口
 * <p>
 * 实现该接口的类将提供:开始「startRecording」、暂停「pauseRecording」、继续「resumeRecording」、停止「stopRecording」方法。
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
    void stopRecording();


}
