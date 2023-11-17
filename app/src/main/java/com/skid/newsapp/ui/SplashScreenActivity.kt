package com.skid.newsapp.ui

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.skid.newsapp.appComponent
import com.skid.newsapp.databinding.ActivitySplashScreenBinding
import javax.inject.Inject
import javax.inject.Provider

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    @Inject
    lateinit var viewModelProvider: Provider<SplashScreenViewModel.Factory>
    private val splashScreenViewModel: SplashScreenViewModel by viewModels { viewModelProvider.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
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