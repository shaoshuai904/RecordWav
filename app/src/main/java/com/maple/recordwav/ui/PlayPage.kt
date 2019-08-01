package com.maple.recordwav.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.maple.recorder.player.PlayDialog
import com.maple.recorder.player.PlayUtils
import com.maple.recordwav.R
import com.maple.recordwav.WavApp
import com.maple.recordwav.databinding.FragmentAudioListBinding
import com.maple.recordwav.utils.SearchFileUtils
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * wav 文件播放
 *
 * @author maple
 * @time 2016/5/20
 */
class PlayPage : BaseFragment() {
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
                        dialogPlay(item)
                    }
                })

        binding.apply {
            tvInfo.text = "WAV 播放界面！"
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
                            "点击条目，播放wav文件 ！"
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


    // 系统播放
    private fun systemPlay(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), "audio/MP3")
        startActivity(intent)
    }

    private fun dialogPlay(file: File) {
        PlayDialog(activity)
                .addWavFile(file)
                .showDialog()
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

    private fun uitlsPlay(file: File) {
        if (playUtils.isPlaying) {
            playUtils.stopPlaying()
        } else {
            playUtils.startPlaying(file.path)
        }
    }

}