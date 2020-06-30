package com.maple.recordwav.ui

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.maple.recorder.recording.AudioRecordConfig
import com.maple.recorder.recording.MsRecorder
import com.maple.recorder.recording.PullTransport
import com.maple.recorder.recording.Recorder
import com.maple.recordwav.R
import com.maple.recordwav.WavApp
import com.maple.recordwav.base.BaseFragment
import com.maple.recordwav.databinding.FragmentRecordBinding
import com.maple.recordwav.utils.DateUtils
import com.maple.recordwav.utils.T
import java.io.File

/**
 * 录制 WavRecorder 界面
 *
 * @author maple
 * @time 2016/4/18
 */
class RecordPage : BaseFragment() {
    private lateinit var binding: FragmentRecordBinding
    private var isRecording = false
    private var curBase: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var recorder = getRecorder()
        updateRecordStatus(RecordStatus.NoStart)

        binding.apply {
            btStart.setOnClickListener {
                recorder.startRecording()
                updateRecordStatus(RecordStatus.Recording)
            }
            btStop.setOnClickListener {
                recorder.stopRecording()
                updateRecordStatus(RecordStatus.Stop)
            }
            btPauseResume.setOnClickListener {
                if (isRecording) {
                    recorder.pauseRecording()
                    updateRecordStatus(RecordStatus.Pause)
                } else {
                    recorder.resumeRecording()
                    updateRecordStatus(RecordStatus.Resume)
                }
            }
            skipSilence.setOnCheckedChangeListener { _, isChecked ->
                recorder = if (isChecked) {
                    getNoiseRecorder()
                } else {
                    getRecorder()
                }
            }
        }

    }

    enum class RecordStatus {
        NoStart,
        Recording,
        Pause,
        Resume,
        Stop
    }

    // update UI
    private fun updateRecordStatus(status: RecordStatus) {
        when (status) {
            // 未开始   停止
            RecordStatus.NoStart,
            RecordStatus.Stop -> {
                isRecording = false
                binding.apply {
                    skipSilence.isEnabled = true
                    btStart.isEnabled = true
                    btPauseResume.isEnabled = false
                    btStop.isEnabled = false

                    btPauseResume.text = getString(R.string.pause)
                    ivVoiceImg.setImageResource(R.drawable.mic_default)
                }
                // time
                curBase = 0
                binding.comVoiceTime.stop()
                animateVoice(0f)
            }
            // 暂停
            RecordStatus.Pause -> {
                isRecording = false
                binding.apply {
                    skipSilence.isEnabled = false
                    btStart.isEnabled = false
                    btPauseResume.isEnabled = true
                    btStop.isEnabled = true

                    btPauseResume.text = getString(R.string.resume)
                    ivVoiceImg.setImageResource(R.drawable.mic_default)
                }
                // time
                curBase = SystemClock.elapsedRealtime() - binding.comVoiceTime.base
                binding.comVoiceTime.stop()
                animateVoice(0f)
            }
            // 录制ing   重新开始
            RecordStatus.Recording,
            RecordStatus.Resume -> {
                isRecording = true
                binding.apply {
                    skipSilence.isEnabled = false
                    btStart.isEnabled = false
                    btPauseResume.isEnabled = true
                    btStop.isEnabled = true

                    btPauseResume.text = getString(R.string.pause)
                    ivVoiceImg.setImageResource(R.drawable.mic_selected)
                }
                // time
                binding.comVoiceTime.base = SystemClock.elapsedRealtime() - curBase
                binding.comVoiceTime.start()
            }
        }
    }

    // 获取普通录音机
    private fun getRecorder(): Recorder {
        return MsRecorder.wav(
                File(getVoicePath()),
                AudioRecordConfig(),
                // AudioRecordConfig(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT, AudioFormat.CHANNEL_IN_MONO, 44100),
                PullTransport.Default().setOnAudioChunkPulledListener { audioChunk ->
                    Log.e("数据监听", "最大值 : ${audioChunk.maxAmplitude()} ")
                    animateVoice((audioChunk.maxAmplitude() / 200.0).toFloat())
                }
        )
    }

    // 获取降噪录音机，跳过沉默区，只录"有声音"的部分
    private fun getNoiseRecorder(): Recorder {
        return MsRecorder.wav(
                File(getVoicePath()),
                AudioRecordConfig(),
                PullTransport.Noise()
                        // 数据监听
                        .setOnAudioChunkPulledListener { audioChunk ->
                            Log.e("数据监听", "最大值 : ${audioChunk.maxAmplitude()} ")
                            animateVoice((audioChunk.maxAmplitude() / 200.0).toFloat())
                        }
                        // 沉默监听
                        .setOnSilenceListener { silenceTime, discardTime ->
                            val message = "沉默时间：$silenceTime ,丢弃时间：$discardTime"
                            Log.e("降噪模式", message)
                            T.showShort(mContext, message)
                        })
    }

    // 录音文件存储名称
    private fun getVoicePath(): String {
        val name = "wav-" + DateUtils.date2Str("yyyy-MM-dd-HH-mm-ss")
        return WavApp.rootPath + name + ".wav"
    }

    private fun animateVoice(maxPeak: Float) {
        if (maxPeak < 0f || maxPeak > 0.5f) {
            return
        }
        binding.ivVoiceImg.animate()
                .scaleX(1 + maxPeak)
                .scaleY(1 + maxPeak)
                .setDuration(10)
                .start()
    }

}