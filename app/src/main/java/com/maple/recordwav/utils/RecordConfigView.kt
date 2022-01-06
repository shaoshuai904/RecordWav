package com.maple.recordwav.utils

import android.content.Context
import android.media.AudioFormat
import android.media.MediaRecorder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.maple.recorder.recording.AudioRecordConfig
import com.maple.recordwav.R
import com.maple.recordwav.databinding.ViewRecordConfigBinding


/**
 * 录音配置View
 *
 * @author : shaoshuai
 * @date ：2021/3/1
 */
class RecordConfigView : FrameLayout {
    private lateinit var binding: ViewRecordConfigBinding
    private var recordConfig = AudioRecordConfig() // 参数配置

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        binding = ViewRecordConfigBinding.inflate(LayoutInflater.from(context), this, true)
        updateConfig(recordConfig)
        with(binding) {
            // 音频源
            rgAudioSource.setOnCheckedChangeListener { _, checkedId ->
                recordConfig.audioSource = when (checkedId) {
                    R.id.rb_default -> MediaRecorder.AudioSource.DEFAULT
                    R.id.rb_source_mic -> MediaRecorder.AudioSource.MIC
                    else -> MediaRecorder.AudioSource.MIC
                }
                onConfigChangeListener?.onConfigChange(recordConfig)
            }
            // 声道数
            rgChannel.setOnCheckedChangeListener { _, checkedId ->
                recordConfig.channelConfig = when (checkedId) {
                    R.id.rb_channel_mono -> AudioFormat.CHANNEL_IN_MONO
                    R.id.rb_channel_stereo -> AudioFormat.CHANNEL_IN_STEREO
                    else -> AudioFormat.CHANNEL_IN_STEREO
                }
                onConfigChangeListener?.onConfigChange(recordConfig)
            }
            // 采样率
            rgSampleRate.setOnCheckedChangeListener { _, checkedId ->
                recordConfig.sampleRateInHz = when (checkedId) {
                    R.id.rb_44100hz -> 44100
                    R.id.rb_22050hz -> 22050
                    R.id.rb_16000hz -> 16000
                    R.id.rb_11025hz -> 11025
                    else -> 11025
                }
                onConfigChangeListener?.onConfigChange(recordConfig)
            }
            // 数据格式
            rgAudioFormat.setOnCheckedChangeListener { _, checkedId ->
                recordConfig.audioFormat = when (checkedId) {
                    R.id.rb_8bit -> AudioFormat.ENCODING_PCM_8BIT
                    R.id.rb_16it -> AudioFormat.ENCODING_PCM_16BIT
                    R.id.rb_float -> AudioFormat.ENCODING_PCM_FLOAT
                    else -> AudioFormat.ENCODING_PCM_16BIT
                }
                onConfigChangeListener?.onConfigChange(recordConfig)
            }
        }
    }

    fun updateConfig(newConfig: AudioRecordConfig) {
        recordConfig = newConfig
        when (recordConfig.audioSource) {
            MediaRecorder.AudioSource.DEFAULT -> binding.rgAudioSource.check(R.id.rb_default)
            MediaRecorder.AudioSource.MIC -> binding.rgAudioSource.check(R.id.rb_source_mic)
            else -> binding.rgAudioSource.clearCheck()
        }
        when (recordConfig.channelConfig) {
            AudioFormat.CHANNEL_IN_MONO -> binding.rgChannel.check(R.id.rb_channel_mono)
            AudioFormat.CHANNEL_IN_STEREO -> binding.rgChannel.check(R.id.rb_channel_stereo)
            else -> binding.rgChannel.clearCheck()
        }
        when (recordConfig.sampleRateInHz) {
            44100 -> binding.rgSampleRate.check(R.id.rb_44100hz)
            22050 -> binding.rgSampleRate.check(R.id.rb_22050hz)
            16000 -> binding.rgSampleRate.check(R.id.rb_16000hz)
            11025 -> binding.rgSampleRate.check(R.id.rb_11025hz)
            else -> binding.rgSampleRate.clearCheck()
        }
        when (recordConfig.audioFormat) {
            AudioFormat.ENCODING_PCM_8BIT -> binding.rgAudioFormat.check(R.id.rb_8bit)
            AudioFormat.ENCODING_PCM_16BIT -> binding.rgAudioFormat.check(R.id.rb_16it)
            AudioFormat.ENCODING_PCM_FLOAT -> binding.rgAudioFormat.check(R.id.rb_float)
            else -> binding.rgAudioFormat.clearCheck()
        }
    }

    var onConfigChangeListener: OnRecordConfigChangeListener? = null

    interface OnRecordConfigChangeListener {
        fun onConfigChange(config: AudioRecordConfig)
    }

}