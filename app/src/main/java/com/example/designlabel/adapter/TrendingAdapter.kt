package com.example.designlabel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.designlabel.R
import com.example.designlabel.interfacecallback.TemplateClickCallBack

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


    }

    override fun getItemCount(): Int {
        return 20
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {

            itemView.setOnClickListener {
                callBack.onItemClickListener("$adapterPosition", categoryName)
            }

        }

    }
}