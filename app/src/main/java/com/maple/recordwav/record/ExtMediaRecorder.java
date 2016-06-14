package com.maple.recordwav.record;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;

/**
 * MediaRecorder录制wav［有损录制］
 *
 * @author maple
 * @time 16/5/20 下午6:47
 */
public class ExtMediaRecorder {

    public enum State {
        INITIALIZING,// 初始化
        READY,// 已经初始化，但没有开始
        RECORDING,// 记录ing
        ERROR,// 需要重建
        STOPPED// 需要重置
    }

    // 用于压缩音质的记录器
    private MediaRecorder mediaRecorder = null;
    // Output file path
    private String filePath = null;
    // 记录状态
    private State state;


    /**
     * 获取实例
     *
     * @return
     */
    public static ExtMediaRecorder getInstance() {
        ExtMediaRecorder result = new ExtMediaRecorder();
        return result;
    }

    /**
     * Instantiates a new recorder, in case of compressed recording the parameters can be left as 0.
     * In case of errors, no exception is thrown, but the state is set to ERROR
     */
    private ExtMediaRecorder() {
        try {// 压缩
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            filePath = null;
            state = State.INITIALIZING;
        } catch (Exception e) {
            Log.e(ExtMediaRecorder.class.getName(), e.getMessage());
            state = State.ERROR;
        }
    }

    /**
     * Sets output file path, call directly after construction/reset.
     *
     * @param argPath file path
     */
    public void setOutputFile(String argPath) {
        try {
            if (state == State.INITIALIZING) {
                filePath = argPath;
                mediaRecorder.setOutputFile(filePath);
            }
        } catch (Exception e) {
            Log.e(ExtMediaRecorder.class.getName(), e.getMessage());
            state = State.ERROR;
        }
    }

    /**
     * Returns the largest amplitude sampled since the last call to this method.
     *
     * @return returns the largest amplitude since the last call, or 0 when not in recording state.
     */
    public int getMaxAmplitude() {
        if (state == State.RECORDING) {
            return mediaRecorder.getMaxAmplitude();
        } else {
            return 0;
        }
    }


    /**
     * Prepares the recorder for recording, in case the recorder is not in the INITIALIZING state and the file path was not set
     * the recorder is set to the ERROR state, which makes a reconstruction necessary.
     * In case uncompressed recording is toggled, the header of the wave file is written.
     * In case of an exception, the state is changed to ERROR
     */
    public void prepare() {
        try {
            if (state == State.INITIALIZING) {
                mediaRecorder.prepare();
                state = State.READY;
            } else {
                Log.e(ExtMediaRecorder.class.getName(), "prepare() method called on illegal state");
                release();
                state = State.ERROR;
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(ExtMediaRecorder.class.getName(), e.getMessage());
            } else {
                Log.e(ExtMediaRecorder.class.getName(), "Unknown error occured in prepare()");
            }
            state = State.ERROR;
        }
    }

    /**
     * Releases the resources associated with this class, and removes the unnecessary files, when necessary
     */
    public void release() {
        if (state == State.RECORDING) {
            stop();
        } else {
            if ((state == State.READY)
//                    & (rUncompressed)
                    ) {
//                try {
//                    randomAccessWriter.close(); // Remove prepared file
//                } catch (IOException e) {
//                    Log.e(ExtMediaRecorder.class.getName(), "I/O exception occured while closing output file");
//                }
                (new File(filePath)).delete();
            }
        }

        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
    }

    /**
     * Resets the recorder to the INITIALIZING state, as if it was just created.
     * In case the class was in RECORDING state, the recording is stopped.
     * In case of exceptions the class is set to the ERROR state.
     */
    public void reset() {
        try {
            if (state != State.ERROR) {
                release();
                filePath = null; // Reset file path

                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                state = State.INITIALIZING;
            }
        } catch (Exception e) {
            Log.e(ExtMediaRecorder.class.getName(), e.getMessage());
            state = State.ERROR;
        }
    }

    /**
     * Starts the recording, and sets the state to RECORDING. Call after prepare().
     */
    public void start() {
        if (state == State.READY) {

            mediaRecorder.start();

            state = State.RECORDING;
        } else {
            Log.e(ExtMediaRecorder.class.getName(), "start() called on illegal state");
            state = State.ERROR;
        }
    }

    /**
     * Stops the recording, and sets the state to STOPPED.
     * In case of further usage, a reset is needed.
     * Also finalizes the wave file in case of uncompressed recording.
     */
    public void stop() {
        if (state == State.RECORDING) {
            mediaRecorder.stop();
            state = State.STOPPED;
        } else {
            Log.e(ExtMediaRecorder.class.getName(), "stop() called on illegal state");
            state = State.ERROR;
        }
    }

}
