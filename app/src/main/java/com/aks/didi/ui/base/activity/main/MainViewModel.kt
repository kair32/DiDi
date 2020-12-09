package com.aks.didi.ui.base.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aks.didi.model.CacheData
import com.aks.didi.network.Status
import com.aks.didi.ui.base.viewmodel.ViewModelBase
import com.aks.didi.utils.FragmentViewModel
import com.aks.didi.utils.PreferencesBasket
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
    fun setToken()
}

class MainViewModelImpl(
    private val preferences: PreferencesBasket
): ViewModelBase(), MainViewModel{
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
        if (preferences.getCookie() == null)
            setToken()
        else{
            CacheData.sid.value = preferences.getCookie()!!
            requestWithCallback({api.checkToken(CacheData.sid.value!!)},
                {
                    replaceFragment(FragmentEvent(
                        when(preferences.getDataSuccessful()){
                            0       -> FragmentType.TAKE_PHONE
                            1       -> FragmentType.TAKE_DOC
                            2       -> FragmentType.INFORMATION
                            else    -> FragmentType.TAKE_PHONE
                }))
                },
                {
                    preferences.setCookie(null)
                    preferences.setDataSuccessful(0)
                    setToken()
                }
            )
        }
    }

    override fun setToken(){
        requestWithCallback({api.auth()},
            { auth->
                auth.accessToken?.let {
                    preferences.setCookie(it)
                    CacheData.sid.postValue(it)
                }
                replaceFragment(FragmentEvent(FragmentType.TAKE_PHONE))
            },
            { showPopUp(it)}
        )
    }
}