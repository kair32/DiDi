package com.aks.didi.ui.base.activity.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.aks.didi.R
import com.aks.didi.databinding.ActivityMainBinding
import com.aks.didi.utils.SharedViewModel
import com.aks.didi.utils.fragment.FragmentUtil
import com.aks.didi.utils.shared.SharedViewModelImpl

class MainActivity: AppCompatActivity() {
    private val fragmentUtil = FragmentUtil

    private lateinit var viewModel: MainViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private fun init(){
        viewModel = ViewModelProvider(this).get(MainViewModelImpl::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModelImpl::class.java)
        fragmentUtil.observe(this, viewModel, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel

        sharedViewModel.popUpLiveData.observe(this){ if (it.isNotBlank()) viewModel.showPopUp(it) }
        sharedViewModel.popUpLiveDataInt.observe(this){
            if (it!=null)
                viewModel.showPopUp(this.resources.getString(it))
        }
        binding.lifecycleOwner = this
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}