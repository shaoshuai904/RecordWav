package com.maple.recordwav.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.maple.recordwav.R
import com.maple.recordwav.databinding.ItemVideoViewBinding
import java.io.File

/**
 *
 * @author maple
 * @time 2019-07-25
 */
class AudioAdapter(var mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mData: List<File> = ArrayList()

    override fun getItemCount() = mData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemVideoViewBinding>(
                LayoutInflater.from(mContext), R.layout.item_video_view, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val taskItem = mData[position]
        (holder as ItemViewHolder).bindView(taskItem)
        holder.binding.root.setOnClickListener {
            mItemClickListener?.onclick(taskItem)
        }
    }

    class ItemViewHolder(val binding: ItemVideoViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(file: File) {
            binding.apply {
                tvTitle.text = file.name
                tvSize.text = getFileSize(file)
                tvType.text = file.extension
            }
        }

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

    fun refresh(dataList: List<File>?) {
        mData = dataList ?: ArrayList()
        this.notifyDataSetChanged()
    }

    // ----------------- item click ----------------------
    private var mItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onclick(item: File)
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener): AudioAdapter {
        this.mItemClickListener = itemClickListener
        return this
    }
}
