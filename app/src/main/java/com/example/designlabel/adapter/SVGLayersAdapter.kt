package com.example.designlabel.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.designlabel.R
import com.example.designlabel.utils.App

class SVGLayersAdapter(
    private var list: ArrayList<String>,
    itemCallBack: SvgLayersClick
) :
    RecyclerView.Adapter<SVGLayersAdapter.ViewHolder>() {

    private var callBack: SvgLayersClick = itemCallBack
    private var selection: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SVGLayersAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layer_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SVGLayersAdapter.ViewHolder, position: Int) {

        Glide.with(App.context)
            .load(list[position])
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.thumbNail)

        holder.layerSelection.isSelected = position == selection

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var thumbNail: ImageView = itemView.findViewById(R.id.imageView52)
        var layerSelection :ConstraintLayout = itemView.findViewById(R.id.layer)

        init {

            itemView.setOnClickListener {
                callBack.setOnLayersClickListener(adapterPosition)
                setSelection(adapterPosition)
            }

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelection(position: Int) {
        selection = position
        notifyDataSetChanged()
    }

    interface SvgLayersClick {
        fun setOnLayersClickListener(position: Int)
    }
}