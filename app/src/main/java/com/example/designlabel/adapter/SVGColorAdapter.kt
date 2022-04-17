package com.example.designlabel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.designlabel.R
import com.example.designlabel.utils.Constant

class SVGColorAdapter(callBackItem: SvgColorClick) :
    RecyclerView.Adapter<SVGColorAdapter.ViewHolder>() {

    private var context: Context? = null
    private var callBack: SvgColorClick = callBackItem


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SVGColorAdapter.ViewHolder {

        context = parent.context

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.svg_color_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: SVGColorAdapter.ViewHolder, position: Int) {
        holder.thumbNail.setColorFilter(
            ContextCompat.getColor(
                context!!,
                Constant.colorArray[position]
            )
        )
    }


    override fun getItemCount(): Int {
        return Constant.colorArray.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var thumbNail: AppCompatImageView

        init {

            thumbNail = itemView.findViewById(R.id.imageView18)

            itemView.setOnClickListener {
                callBack.setOnColorClickListener(adapterPosition)
            }

        }

    }

    interface SvgColorClick {
        fun setOnColorClickListener(position: Int)
    }
}