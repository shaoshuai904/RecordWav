package com.maple.recordwav.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.maple.recorder.parse.WaveFileReader
import com.maple.recordwav.R
import com.maple.recordwav.WavApp
import com.maple.recordwav.databinding.FragmentAudioListBinding
import com.maple.recordwav.utils.SearchFileUtils
import com.maple.recordwav.utils.T
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*

/**
 * 获取wav文件的信息
 *
 * @author maple
 * @time 2016/5/20
 */
class ParsePage : BaseFragment() {
    lateinit var binding: FragmentAudioListBinding
    lateinit var adapter: AudioAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio_list, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        searchFile()
    }

    private fun initView() {
        adapter = AudioAdapter(mContext, null)
                .setOnItemClickListener(object : AudioAdapter.OnItemClickListener {
                    override fun onclick(item: File) {
                        getWavInfo(item.absolutePath)
                    }
                })

        binding.apply {
            tvInfo.text = "WAV 解析界面！"
            rvVideo.adapter = adapter

            srlRefreshLayout
                    .setRefreshHeader(ClassicsHeader(mContext))
                    .setOnRefreshListener { searchFile() }
                    .isEnableLoadMore = false
        }
    }

    private fun searchFile() {
        Observable.just(File(WavApp.rootPath))
                .map {
                    SearchFileUtils.search(it, arrayOf(".wav")).apply {
                        sortWith(Comparator { o1, o2 ->
                            o2.name.compareTo(o1.name)
                        })
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<File>> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(files: List<File>) {
                        binding.tvInfo.text = if (files.isNotEmpty()) {
                            "点击条目，获取wav文件的信息 ！"
                        } else {
                            "没有找到文件，请去录制 ！"
                        }
                        binding.srlRefreshLayout.finishRefresh()
                        adapter.refresh(files)
                    }

                    override fun onError(e: Throwable) {
                        Log.e("search error", "e: $e")
                    }

                    override fun onComplete() {}
                })
    }

    private fun getWavInfo(filename: String) {
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