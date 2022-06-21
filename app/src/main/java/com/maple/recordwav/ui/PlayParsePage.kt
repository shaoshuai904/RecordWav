package com.maple.recordwav.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maple.recorder.parse.WaveFileReader
import com.maple.recorder.player.PlayDialog
import com.maple.recorder.player.PlayUtils
import com.maple.recordwav.WavApp
import com.maple.recordwav.databinding.FragmentAudioListBinding
import com.maple.recordwav.utils.SearchFileUtils
import com.maple.recordwav.utils.T
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * wav文件 播放/解析
 *
 * @author maple
 * @time 2016/5/20
 */
class PlayParsePage : BaseFragment() {
    private lateinit var binding: FragmentAudioListBinding
    private val mAdapter by lazy {
        AudioAdapter(mContext).apply {
            setOnItemClickListener { item, _ -> dialogPlay(item) }
            setOnItemLongClickListener { _, position ->
                getWavInfo(getItem(position))
                true
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAudioListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        searchFile()
    }

    private fun initView() {
        binding.apply {
            tvInfo.text = "WAV 播放界面！"
            rvVideo.adapter = mAdapter
            srlRefreshLayout.setRefreshHeader(ClassicsHeader(mContext))
                    .setOnRefreshListener { searchFile() }
                    .setEnableLoadMore(false)
        }
    }

    @SuppressLint("CheckResult")
    private fun searchFile() {
        Observable.just(WavApp.saveFile)
                .map {
                    SearchFileUtils.search(it, arrayOf(".wav")).apply {
                        sortWith { o1, o2 ->
                            o2.name.compareTo(o1.name)
                        }
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    updateVideoData(it)
                }
    }

    private fun updateVideoData(files: List<File>) {
        binding.apply {
            tvInfo.text = if (files.isNotEmpty()) {
                "点击条目播放wav文件, \n长按条目获取文件详细信息！"
            } else {
                "没有找到文件，请去录制 ！"
            }
            srlRefreshLayout.finishRefresh()
            mAdapter.refreshData(files)
        }
    }

    //----------------------------------播放----------------------------------------
    // 1：系统播放
    private fun systemPlay(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), "audio/MP3")
        startActivity(intent)
    }

    // 2：dialog播放
    private fun dialogPlay(file: File) {
        PlayDialog(activity)
                .addWavFile(file)
                .showDialog()
    }

    // 3：工具类播放
    private fun uitlsPlay(file: File) {
        if (playUtils.isPlaying) {
            playUtils.stopPlaying()
        } else {
            playUtils.startPlaying(file.path)
        }
    }

    private val playUtils by lazy {
        PlayUtils().setPlayStateChangeListener {
            if (it) {
//                // startTimer
//                com_voice_time.setBase(SystemClock.elapsedRealtime())
//                com_voice_time.start()
//                bt_preview.setText(getResources().getString(R.string.stop))
//                iv_voice_img.setImageResource(R.drawable.mic_selected)
            } else {
//                com_voice_time.stop();
//                com_voice_time.setBase(SystemClock.elapsedRealtime())
//                bt_preview.setText(getResources().getString(R.string.preview))
//                iv_voice_img.setImageResource(R.drawable.mic_default)
            }
        }
    }

    //----------------------------------解析----------------------------------------

    private fun getWavInfo(file: File) {
        val filename = file.absolutePath
        val reader = WaveFileReader(filename)
        if (reader.isSuccess) {
            binding.tvInfo.text = ("读取wav文件信息：" + filename
                    + "\n采样率：" + reader.sampleRate
                    + "\n声道数：" + reader.numChannels
                    + "\n编码长度：" + reader.bitPerSample
                    + "\n数据长度：" + reader.dataLen)
        } else {
            T.showShort(mContext, filename + "不是一个正常的wav文件")
        }
    }
}