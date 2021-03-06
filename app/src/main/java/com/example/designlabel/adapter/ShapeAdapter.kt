package com.example.designlabel.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.designlabel.R
import com.example.designlabel.customCallBack.StickerClick
import com.example.designlabel.utils.loadThumbnail

class ShapeAdapter(callBack: StickerClick) : RecyclerView.Adapter<ShapeAdapter.ViewHolder>() {

    private val itemCallBAck: StickerClick = callBack
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShapeAdapter.ViewHolder {
        context = parent.context

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layer_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShapeAdapter.ViewHolder, position: Int) {

        val path = "file:///android_asset/category/shape/${position + 1}.png"

        Log.d("myShapePath", path)

        holder.thumbNail.loadThumbnail(path, null)
    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var thumbNail: ImageView = itemView.findViewById(R.id.imageView52)

        init {

            itemView.setOnClickListener {
                itemCallBAck.setOnStickerClickListener(adapterPosition + 1, true)
            }

        }

    }

}