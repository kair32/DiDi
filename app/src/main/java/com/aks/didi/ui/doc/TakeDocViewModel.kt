package com.aks.didi.ui.doc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aks.didi.R
import com.aks.didi.model.CacheData
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
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

interface TakeDocViewModel: FragmentViewModel, SharedViewModel, PermissionViewModel {
    var imagePath: String
    val isPermissionCameraGranted: LiveData<Boolean>
    val isNextEnabled: LiveData<Boolean>
    val takePhoto: LiveData<DocItem>

    fun onNext()
    fun onTakePhotoSuccess(file: File)
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
        if (isPermissionCameraGranted.value == true) {
            takePhoto.postValue(item)
            replaceFragment(FragmentEvent(FragmentType.CHOOSE_PHOTO))
        }
        else checkPermission(PermissionEvent(PermissionType.CAMERA))

    override fun onNext() = replaceFragment(FragmentEvent(FragmentType.INFORMATION))

    override fun onTakePhotoSuccess(file: File) {
        val part = MultipartBody.Part.createFormData("files", null, file.asRequestBody("image/".toMediaTypeOrNull()))
        requestWithCallback({api.loadImage(CacheData.sid,part)},{

        },{

        })
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) { if (response?.permissionName == PermissionType.CAMERA.permission) isPermissionCameraGranted.postValue(true) }
    override fun onPermissionDenied(response: PermissionDeniedResponse?) = showPopUp(R.string.access_to_camera)
    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) = token?.continuePermissionRequest() ?: Unit

    //region maybe
    private fun createPart(key: String, value: String)
            = MultipartBody.Builder().addPart(createHeaders(disposition(key)), value.toRequestBody())

    private fun disposition(key: String) = buildString {
        append("form-data; name=")
        appendQuotedString(key)
    }

    private fun createHeaders(disposition: String) = Headers.Builder()
            .addUnsafeNonAscii("Content-Disposition", disposition)
            .addUnsafeNonAscii("content-type","multipart/form-data;boundary=-------------573cf973d5228")
            .build()

    private fun StringBuilder.appendQuotedString(key: String) {
        append('"')
        for (i in 0 until key.length) {
            when (val ch = key[i]) {
                '\n' -> append("%0A")
                '\r' -> append("%0D")
                '"' -> append("%22")
                else -> append(ch)
            }
        }
        append('"')
    }
    //endregion
}