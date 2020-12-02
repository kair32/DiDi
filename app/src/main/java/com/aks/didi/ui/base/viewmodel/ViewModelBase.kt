package com.aks.didi.ui.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aks.didi.utils.ActivityStartViewModel
import com.aks.didi.utils.FragmentViewModel
import com.aks.didi.utils.SharedViewModel
import com.aks.didi.utils.activity.ActivityStartEvent
import com.aks.didi.utils.fragment.FragmentEvent

abstract class ViewModelBase: ViewModel(), FragmentViewModel, ActivityStartViewModel, SharedViewModel {
    protected open val tag: String = "ViewModelBase"
    override val fragmentLiveData: MutableLiveData<FragmentEvent> by lazy { MutableLiveData<FragmentEvent>() }
    override val activityStartLiveData: MutableLiveData<ActivityStartEvent> by lazy { MutableLiveData<ActivityStartEvent>() }
    override val popUpLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    protected fun replaceFragment(event: FragmentEvent) = fragmentLiveData.postValue(event)
    protected fun startActivity(event: ActivityStartEvent) = activityStartLiveData.postValue(event)
    protected open fun showPopUp(text: String) = popUpLiveData.postValue(text)
}