package com.example.designlabel.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.designlabel.adapter.TrendingAdapter
import com.example.designlabel.databinding.ActivityMainBinding
import com.example.designlabel.interfacecallback.TemplateClickCallBack
import com.example.designlabel.pageradapter.ViewPagerAdapter
import com.example.designlabel.utils.Constant
import com.example.designlabel.utils.Utils
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), TemplateClickCallBack {

    private lateinit var binding: ActivityMainBinding
    private val workerHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.icProfile.setOnClickListener {
            Utils.showToast(this, "Click Profile")
        }

        binding.icPro.setOnClickListener {
            Utils.showToast(this, "Calling Pro Click")
        }

        workerHandler.post {
            updateTrendingAdapter()
            updateViewPagerAdapter()
        }

    }

    private fun updateTrendingAdapter() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = TrendingAdapter("ramzan", this)
    }

    private fun updateViewPagerAdapter() {
        binding.viewPager.adapter = createCardAdapter()
        TabLayoutMediator(
            binding.tabs, binding.viewPager
        ) { tab, position -> tab.text = Constant.listOfCategory[position] }.attach()
    }

    override fun onItemClickListener(labelNumber: String, categoryName: String) {
        startActivity(Intent(this, EditingScreen::class.java))
    }

    private fun createCardAdapter(): ViewPagerAdapter {
        return ViewPagerAdapter(this@MainActivity)
    }


}