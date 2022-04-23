package com.example.designlabel.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.designlabel.R
import com.example.designlabel.adapter.NewLabelAdapter
import com.example.designlabel.utils.Constant

class CategoryFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var mAdapter: NewLabelAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewfategoryfragment)
        recyclerView!!.setHasFixedSize(true)
        adaptersCall()
        return view
    }

    private fun adaptersCall() {
        mAdapter = NewLabelAdapter(Constant.listOfCategory[Constant.categoryPosition])
        recyclerView!!.adapter = mAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(counter: Int): CategoryFragment {
            Constant.categoryPosition = counter
            return CategoryFragment()
        }
    }

}