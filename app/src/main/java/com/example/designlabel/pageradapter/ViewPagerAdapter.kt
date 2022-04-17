package com.example.designlabel.pageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.designlabel.fragments.CategoryFragment.Companion.newInstance
import com.example.designlabel.utils.Constants

class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return newInstance(position)
    }

    override fun getItemCount(): Int {
        return Constants.categoryMap.size
    }

    companion object {
        private const val CARD_ITEM_SIZE = 10
    }
}