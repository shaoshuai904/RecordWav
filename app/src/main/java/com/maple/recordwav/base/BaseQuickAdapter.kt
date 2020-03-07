package com.maple.recordwav.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 *
 * @author maple
 * @time 2018-08-06
 */
abstract class BaseQuickAdapter<T, K : RecyclerView.ViewHolder> : RecyclerView.Adapter<K>() {
    private var mData: List<T> = emptyList()

    fun refreshData(data: List<T>?) {
        this.mData = data ?: emptyList()
        notifyDataSetChanged()
    }

    fun addData(data: T) {
        this.mData = arrayListOf<T>().apply {
            addAll(mData)
            add(data)
        }
        notifyDataSetChanged()
    }

    fun data() = mData

    fun getItem(position: Int): T = mData[position]

    override fun getItemCount(): Int = mData.size

    protected fun bindViewClickListener(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            if (onItemClickListener != null) {
                viewHolder.itemView.setOnClickListener {
                    onItemClickListener!!.onItemClick(it, position)
                }
            }
            if (onItemLongClickListener != null) {
                viewHolder.itemView.setOnLongClickListener {
                    onItemLongClickListener!!.onItemLongClick(it, position)
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------

    // item click
    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    // item long click
    var onItemLongClickListener: OnItemLongClickListener? = null

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int): Boolean
    }

}
