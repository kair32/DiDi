package com.aks.didi.ui.doc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aks.didi.R
import com.aks.didi.ui.base.viewmodel.ViewModelBase
import com.aks.didi.utils.FragmentViewModel
import com.aks.didi.utils.PermissionViewModel
import com.aks.didi.utils.SharedViewModel
import com.aks.didi.utils.fragment.FragmentEvent
import com.aks.didi.utils.fragment.FragmentType
import com.aks.didi.utils.permissions.PermissionEvent
import com.aks.didi.utils.permissions.PermissionType
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

interface TakeDocViewModel: FragmentViewModel, SharedViewModel, PermissionViewModel {
    var imagePath: String
    val isPermissionCameraGranted: LiveData<Boolean>
    val isNextEnabled: LiveData<Boolean>
    val takePhoto: LiveData<DocItem>

    fun onNext()
    fun onTakePhotoSuccess()
    fun onTakePhoto(item: DocItem)
}

class TakeDocViewModelImpl: ViewModelBase(), TakeDocViewModel, PermissionListener {
    override var imagePath = ""
    override val listener = this
    override val isPermissionCameraGranted = MutableLiveData(false)
    override val isNextEnabled = MutableLiveData(true)
    override val takePhoto = MutableLiveData<DocItem>()

    init {
        checkPermission(PermissionEvent(PermissionType.CAMERA))
    }

    override fun onTakePhoto(item: DocItem) =
        if (isPermissionCameraGranted.value == true) takePhoto.postValue(item)
        else checkPermission(PermissionEvent(PermissionType.CAMERA))

    override fun onNext() = replaceFragment(FragmentEvent(FragmentType.INFORMATION))

    override fun onTakePhotoSuccess() {}

    override fun onPermissionGranted(response: PermissionGrantedResponse?) { if (response?.permissionName == PermissionType.CAMERA.permission) isPermissionCameraGranted.postValue(true) }
    override fun onPermissionDenied(response: PermissionDeniedResponse?) = showPopUp(R.string.access_to_camera)
    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) = token?.continuePermissionRequest() ?: Unit
}