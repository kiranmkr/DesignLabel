package com.example.designlabel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.designlabel.R
import com.example.designlabel.interfacecallback.TemplateClickCallBack
import com.example.designlabel.utils.Constant
import com.example.designlabel.utils.Utils
import com.example.designlabel.utils.loadThumbnail

class TrendingAdapter(var categoryName: String, val callBack: TemplateClickCallBack) :
    RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {

    private var context: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        context = parent.context

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.template_trending_cat_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val path: String =
            "file:///android_asset/category/${
                categoryName
            }/" + (position + 1) + ".png"

        holder.placeHolder.loadThumbnail(path, null)

    }

    override fun getItemCount(): Int {
        return Utils.categoryMap[categoryName]!!.toInt()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var placeHolder: ImageView = itemView.findViewById(R.id.placeHolder)

        init {

            itemView.setOnClickListener {
                Constant.categoryName = categoryName
                Constant.labelNumber = adapterPosition + 1
                callBack.onItemClickListener("$adapterPosition", categoryName)
            }

        }

    }
}