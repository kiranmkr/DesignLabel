package com.example.designlabel.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.designlabel.R
import com.example.designlabel.interfacecallback.TemplateClickCallBack
import com.example.designlabel.ui.MainActivity


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


    }

    override fun getItemCount(): Int {
        return 20
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        init {

            callBack = mContext as MainActivity
            itemView.setOnClickListener {
                callBack?.onItemClickListener("$adapterPosition", categoryName)
            }

        }

    }


}