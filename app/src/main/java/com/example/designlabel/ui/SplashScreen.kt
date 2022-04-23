package com.example.designlabel.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.designlabel.R
import com.example.designlabel.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {

            binding = ActivitySplashScreenBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val myAnim = AnimationUtils.loadAnimation(this@SplashScreen, R.anim.zoom_splash)
            binding.imageView.startAnimation(myAnim)
            binding.imageView2.startAnimation(myAnim)
            myAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    Log.e("myAnimation", "start")
                }

                override fun onAnimationEnd(animation: Animation) {
                    Log.e("myAnimation", "end")
                    nextActivity()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
            nextActivity()
        }
    }

    private fun nextActivity() {
        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
        finish()
    }
}