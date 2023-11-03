package com.skid.newsapp.ui

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skid.newsapp.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.splashScreenLottieAnimationView.addAnimatorListener(object : AnimatorListener {
            override fun onAnimationStart(p0: Animator) = Unit

            override fun onAnimationEnd(p0: Animator) {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            }

            override fun onAnimationCancel(p0: Animator) = Unit
            override fun onAnimationRepeat(p0: Animator) = Unit
        })
    }
}