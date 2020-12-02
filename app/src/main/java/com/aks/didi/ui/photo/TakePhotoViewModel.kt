package com.aks.didi.ui.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aks.didi.ui.base.viewmodel.ViewModelBase
import com.aks.didi.utils.FragmentViewModel
import com.aks.didi.utils.SharedViewModel
import com.aks.didi.utils.fragment.FragmentEvent
import com.aks.didi.utils.fragment.FragmentType

interface TakePhotoViewModel: FragmentViewModel, SharedViewModel {
    val fio: MutableLiveData<String>
    val phone: MutableLiveData<String>
    val isFocusCity: MutableLiveData<Boolean>
    val city: LiveData<String>
    val cities: LiveData<List<String>>
    val isNextEnabled: LiveData<Boolean>

    fun onPhoneEntry(length: Int)
    fun onFioEntry(length: Int)
    fun onCity(city: String)
    fun onCityEntry(str: String)
    fun onNext()
}

class TakePhotoViewModelImpl: ViewModelBase(), TakePhotoViewModel{
    override val phone = MutableLiveData<String>("")
    override val fio = MutableLiveData<String>("")
    override val isFocusCity = MutableLiveData<Boolean>(false)
    override val isNextEnabled = MutableLiveData<Boolean>(true)
    override val city = MutableLiveData<String>("")
    override val cities = MutableLiveData<List<String>>(listOf("Москва","Брянск","Выборг","Курск","Нижни Новгород"))

    private var isPhone: Boolean = false
    private var isCity: Boolean = false
    private var isFio: Boolean = false

    init {
        showPopUp("Привет мир!")
    }

    override fun onPhoneEntry(length: Int){
        isPhone = (length==16)
        onCheckEntry()
    }

    override fun onFioEntry(length: Int){
        isFio = (length > 5)
        onCheckEntry()
    }

    private fun onCheckEntry() {
        val result = isPhone && isCity && isFio
        isNextEnabled.postValue(result)
    }

    override fun onCity(city: String) {
        this.city.value = city
        isFocusCity.postValue(false)
        isCity = true
        onCheckEntry()
    }

    override fun onCityEntry(str: String){
        cities.postValue(cities.value?.filter { it.contains(str, ignoreCase = true) })
    }

    override fun onNext() = replaceFragment(FragmentEvent(FragmentType.TAKE_DOC))
}