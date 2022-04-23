package com.example.designlabel.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.designlabel.R
import com.example.designlabel.interfacecallback.TemplateClickCallBack
import com.example.designlabel.ui.MainActivity
import com.example.designlabel.utils.Constant
import com.example.designlabel.utils.loadThumbnail


class NewLabelAdapter(var categoryName: String) :
    RecyclerView.Adapter<NewLabelAdapter.ViewHolder>() {

    private var mContext: Activity? = null
    private var context: Context? = null
    var callBack: TemplateClickCallBack? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        context = parent.context

        mContext = parent.context as Activity

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.template_sub_cat_item, parent, false)

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
        return Constant.categoryMap[categoryName]!!.toInt()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var placeHolder: ImageView = itemView.findViewById(R.id.placeHolder)

        init {

            callBack = mContext as MainActivity
            itemView.setOnClickListener {
                Constant.categoryName = categoryName
                Constant.labelNumber = adapterPosition + 1
                callBack?.onItemClickListener("$adapterPosition", categoryName)
            }

        }

    }


}