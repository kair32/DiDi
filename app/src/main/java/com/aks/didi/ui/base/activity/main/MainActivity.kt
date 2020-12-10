package com.aks.didi.ui.base.activity.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.aks.didi.R
import com.aks.didi.databinding.ActivityMainBinding
import com.aks.didi.model.CacheData
import com.aks.didi.utils.PreferencesBasket
import com.aks.didi.utils.SharedViewModel
import com.aks.didi.utils.ViewModelFactory
import com.aks.didi.utils.fragment.FragmentUtil
import com.aks.didi.utils.shared.SharedViewModelImpl

class MainActivity: AppCompatActivity() {
    private val fragmentUtil = FragmentUtil

    private lateinit var viewModel: MainViewModel
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var preference: PreferencesBasket

    private fun init(){
        preference = PreferencesBasket(this)
        val factory = ViewModelFactory(preference?:return)
        viewModel = ViewModelProvider(this, factory).get(MainViewModelImpl::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModelImpl::class.java)
        fragmentUtil.observe(this, viewModel, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        if (System.currentTimeMillis() >= 1611175604000) finish()

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.sharedViewModel = sharedViewModel

        sharedViewModel.popUpLiveData.observe(this){ if (it.isNotBlank()) viewModel.showPopUp(it) }
        sharedViewModel.popUpLiveDataInt.observe(this){
            if (it!=null)
                viewModel.showPopUp(this.resources.getString(it))
        }
        sharedViewModel.isLoading.observe(this){ viewModel.isLoadingValue.postValue(it)}

        CacheData.sid.observe(this){
            if (it.isNullOrBlank()) {
                preference.setCookie(null)
                preference.setDataSuccessful(0)
                viewModel.setToken()
            }
        }
        binding.lifecycleOwner = this
    }

    override fun onBackPressed() = viewModel.onBackPressed()

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}