package com.aks.didi.ui.base.activity.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aks.didi.ui.base.activity.main.MainActivity

class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = MainActivity.newIntent(this)
        startActivity(intent)
        finish()
    }
}