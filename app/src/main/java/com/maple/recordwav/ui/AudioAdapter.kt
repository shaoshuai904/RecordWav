package com.maple.recordwav.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.maple.recordwav.R
import com.maple.recordwav.databinding.ItemVideoViewBinding
import java.io.File

/**
 *
 * @author maple
 * @time 2019-07-25
 */
class AudioAdapter(private val context: Context) : BaseAdapter() {
    private var dataList: List<File> = arrayListOf()

    override fun getCount() = dataList.size
    override fun getItem(position: Int) = dataList[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val file = dataList[position]

        val holder: ViewHolder
        val view: View
        if (convertView == null) {
            val binding: ItemVideoViewBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                    R.layout.item_video_view, parent, false)
            holder = ViewHolder(binding)
            view = holder.binding.root
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        holder.binding.apply {
            tvTitle.text = file.name
            tvSize.text = getFileSize(file)
            tvType.text = file.extension
        }
        return view
    }

    fun refresh(datas: List<File>?) {
        dataList = datas ?: arrayListOf()
        this.notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemVideoViewBinding)

    /**
     * 文件大小 B
     */
    private fun getFileSize(file: File): String {
        val size = file.length()
        return when {
            size < 1024 -> "$size B"
            size < (1024 * 1024) -> String.format("%.2f KB", size.div(1024f))
            else -> String.format("%.2f MB", size.div(1024 * 1024f))
        }
    }
}