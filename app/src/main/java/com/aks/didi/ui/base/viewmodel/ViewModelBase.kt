package com.aks.didi.ui.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aks.didi.utils.ActivityStartViewModel
import com.aks.didi.utils.FragmentViewModel
import com.aks.didi.utils.PermissionViewModel
import com.aks.didi.utils.SharedViewModel
import com.aks.didi.utils.activity.ActivityStartEvent
import com.aks.didi.utils.fragment.FragmentEvent
import com.aks.didi.utils.permissions.PermissionEvent
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

abstract class ViewModelBase: ViewModel(), FragmentViewModel, ActivityStartViewModel, SharedViewModel,
    PermissionViewModel {
    protected open val tag: String = "ViewModelBase"
    override val fragmentLiveData: MutableLiveData<FragmentEvent> by lazy { MutableLiveData<FragmentEvent>() }
    override val activityStartLiveData: MutableLiveData<ActivityStartEvent> by lazy { MutableLiveData<ActivityStartEvent>() }
    override val popUpLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    override val popUpLiveDataInt: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    override val permissionLiveData: MutableLiveData<PermissionEvent> by lazy { MutableLiveData<PermissionEvent>() }
    override val listener: PermissionListener get() = TODO("not implemented")

    protected fun replaceFragment(event: FragmentEvent) = fragmentLiveData.postValue(event)
    protected fun startActivity(event: ActivityStartEvent) = activityStartLiveData.postValue(event)
    protected fun checkPermission(event: PermissionEvent) = permissionLiveData.postValue(event)

    protected open fun showPopUp(text: String) = popUpLiveData.postValue(text)
    protected open fun showPopUp(res: Int) = popUpLiveDataInt.postValue(res)
}