package com.maple.recordwav.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maple.msdialog.adapter.BaseQuickAdapter
import com.maple.recordwav.databinding.ItemVideoViewBinding
import com.maple.recordwav.utils.ConversionUtils
import java.io.File

/**
 *
 * @author maple
 * @time 2019-07-25
 */
class AudioAdapter(var mContext: Context) : BaseQuickAdapter<File, RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemVideoViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(getItem(position))
    }

    inner class ItemViewHolder(private val binding: ItemVideoViewBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bindView(file: File) {
            bindViewClickListener(this)
            binding.apply {
                tvTitle.text = file.name
                tvSize.text = ConversionUtils.convertB(file.length())
                tvType.text = file.extension
            }
        }
    }

}
