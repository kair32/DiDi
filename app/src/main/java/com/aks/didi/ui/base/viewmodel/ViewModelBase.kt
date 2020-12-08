package com.aks.didi.ui.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aks.didi.model.CacheData
import com.aks.didi.model.RequestWrapper
import com.aks.didi.network.Api
import com.aks.didi.network.NetworkService
import com.aks.didi.network.Status
import com.aks.didi.utils.ActivityStartViewModel
import com.aks.didi.utils.FragmentViewModel
import com.aks.didi.utils.PermissionViewModel
import com.aks.didi.utils.SharedViewModel
import com.aks.didi.utils.activity.ActivityStartEvent
import com.aks.didi.utils.fragment.FragmentEvent
import com.aks.didi.utils.fragment.FragmentType
import com.aks.didi.utils.fragment.getViewModelFragment
import com.aks.didi.utils.permissions.PermissionEvent
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.internal.LinkedTreeMap
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Response

abstract class ViewModelBase: ViewModel(), FragmentViewModel, ActivityStartViewModel, SharedViewModel,
    PermissionViewModel {
    protected open val tag: String = "ViewModelBase"
    override val isLoading: MutableLiveData<Status> by lazy { MutableLiveData<Status>() }
    var api: Api = NetworkService.retrofitService()
    override val fragmentLiveData: MutableLiveData<FragmentEvent> by lazy { MutableLiveData<FragmentEvent>() }
    override val activityStartLiveData: MutableLiveData<ActivityStartEvent> by lazy { MutableLiveData<ActivityStartEvent>() }
    override val popUpLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    override val popUpLiveDataInt: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    override val permissionLiveData: MutableLiveData<PermissionEvent> by lazy { MutableLiveData<PermissionEvent>() }
    override val listener: PermissionListener get() = TODO("not implemented")

    protected fun replaceFragment(event: FragmentEvent) = fragmentLiveData.postValue(event)
    protected fun startActivity(event: ActivityStartEvent) = activityStartLiveData.postValue(event)
    protected fun checkPermission(event: PermissionEvent) = permissionLiveData.postValue(event)

    protected open fun showPopUp(text: String?) { if (!text.isNullOrBlank()) popUpLiveData.postValue(text)}
    protected open fun showPopUp(res: Int) = popUpLiveDataInt.postValue(res)


    override fun onBackPressed() = fragmentLiveData.postValue(FragmentEvent(FragmentType.BACK, isBack = true))
    //region запросы

    fun <T> requestWithCallback(
            request: suspend () -> Response<T>,
            response: (T) -> Unit,
            errorCallback: ((String?) -> Unit)?) {

        isLoading.postValue(Status.LOADING)

        this.viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = request.invoke()

                launch(Dispatchers.IO) {
                    if (res.body() != null) {
                        if(!(res.body() as RequestWrapper).success){
                            val maps = ((res.body() as RequestWrapper).errors as? LinkedTreeMap<String,String>)
                            maps?.values?.joinToString(", ").let { errorCallback?.invoke(it)}
                            isLoading.postValue(Status.ERROR)
                        } else {
                            checkOldToken(res.code())
                            response(res.body()!!)
                            isLoading.postValue(Status.SUCCESS)
                        }
                    } else if (res.errorBody() != null) {
                        checkOldToken(res.code())
                        errorCallback?.invoke(getError(res.errorBody()))
                        isLoading.postValue(Status.ERROR)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    errorCallback?.invoke(null)
                    isLoading.postValue(Status.ERROR)
                }
            }
        }
    }

    private fun checkOldToken(code: Int){
        if (code == 403)
            CacheData.sid.postValue("")
    }

    private fun getError(errorBody: ResponseBody?): String?{
        val errorsBody = try {
            errorBody?.string()?.let {
                JsonParser().parse(it)?.asJsonObject?.get("errors")?.asJsonObject
            }
        }catch (e: Exception) { null }
        return errorsBody?.entrySet()?.joinToString(", ") { it.value.asString }
    }

    //endregion
}