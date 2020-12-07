package com.aks.didi.utils

import androidx.lifecycle.LiveData
import com.aks.didi.network.Status
import com.aks.didi.utils.activity.ActivityStartEvent
import com.aks.didi.utils.fragment.FragmentEvent
import com.aks.didi.utils.permissions.PermissionEvent
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

abstract class EventBase(var happen: Boolean = false)

interface SharedViewModel {
    val popUpLiveData: LiveData<String>
    val popUpLiveDataInt: LiveData<Int>
    val isLoading: LiveData<Status>
}

interface FragmentViewModel {
    val fragmentLiveData: LiveData<FragmentEvent>
}

interface ActivityStartViewModel {
    val activityStartLiveData: LiveData<ActivityStartEvent>
}

interface PermissionViewModel {
    val listener: PermissionListener
    val permissionLiveData: LiveData<PermissionEvent>
}