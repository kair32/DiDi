package com.aks.didi.utils.shared

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.aks.didi.ui.base.viewmodel.ViewModelBase
import com.aks.didi.utils.SharedViewModel

class SharedViewModelImpl: ViewModelBase(), SharedViewModel{
    override val popUpLiveData = MutableLiveData<String>()
}

object SharedUtil {
    fun observe(owner: LifecycleOwner, viewModel: SharedViewModel, activity: FragmentActivity?){
        if (activity == null) return
        val sharedModel = ViewModelProvider(activity).get(SharedViewModelImpl::class.java)
        viewModel.popUpLiveData.observe(owner,  Observer { sharedModel.popUpLiveData.value = it })
        viewModel.popUpLiveDataInt.observe(owner,  Observer { sharedModel.popUpLiveDataInt.value = it })
    }
}