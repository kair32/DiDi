package com.aks.didi.utils

import androidx.lifecycle.LiveData
import com.aks.didi.utils.activity.ActivityStartEvent
import com.aks.didi.utils.fragment.FragmentEvent

abstract class EventBase(var happen: Boolean = false)

interface SharedViewModel {
    val popUpLiveData: LiveData<String>
}

interface FragmentViewModel {
    val fragmentLiveData: LiveData<FragmentEvent>
}

interface ActivityStartViewModel {
    val activityStartLiveData: LiveData<ActivityStartEvent>
}