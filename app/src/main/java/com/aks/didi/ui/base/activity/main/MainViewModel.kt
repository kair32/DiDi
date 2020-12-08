package com.aks.didi.ui.base.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aks.didi.model.CacheData
import com.aks.didi.network.Status
import com.aks.didi.ui.base.viewmodel.ViewModelBase
import com.aks.didi.utils.FragmentViewModel
import com.aks.didi.utils.fragment.FragmentEvent
import com.aks.didi.utils.fragment.FragmentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface PopUpViewModel {
    val isPopUpVisible: LiveData<Boolean>
    val popUpText: LiveData<String>
    fun showPopUp(text: String?)
}

interface MainViewModel: FragmentViewModel, PopUpViewModel{
    val isLoading: MutableLiveData<Status>
}

class MainViewModelImpl: ViewModelBase(), MainViewModel{
    override val isPopUpVisible = MutableLiveData<Boolean>()
    override val popUpText = MutableLiveData<String>("")

    override fun showPopUp(text: String?) {
        if (text.isNullOrBlank()) return
        popUpText.postValue(text)
        isPopUpVisible.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            delay(4000)
            isPopUpVisible.postValue(false)
            popUpText.postValue("")
        }
    }

    init {
        requestWithCallback({api.auth()},
                { auth->
                    auth.accessToken?.let { CacheData.sid = it}
                    replaceFragment(FragmentEvent(FragmentType.TAKE_PHONE))
                },
                { showPopUp(it)}
        )
    }
}