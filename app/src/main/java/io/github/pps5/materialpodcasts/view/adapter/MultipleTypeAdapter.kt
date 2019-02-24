package io.github.pps5.materialpodcasts.view.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class MultipleTypeAdapter : RecyclerView.Adapter<MultipleTypeAdapter.BaseViewHolder>() {

    protected fun <T : ViewDataBinding> ViewGroup.inflate(layoutId: Int): T {
        return DataBindingUtil.inflate(
                LayoutInflater.from(context), layoutId, this, false)
    }

    abstract class BaseViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(position: Int)
    }

}