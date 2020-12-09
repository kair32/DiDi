package com.aks.didi.ui.doc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aks.didi.R
import com.aks.didi.model.CacheData
import com.aks.didi.ui.base.viewmodel.ViewModelBase
import com.aks.didi.utils.FragmentViewModel
import com.aks.didi.utils.PermissionViewModel
import com.aks.didi.utils.PreferencesBasket
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
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


interface TakeDocViewModel: FragmentViewModel, SharedViewModel, PermissionViewModel {
    var imagePath: String
    val isPermissionCameraGranted: LiveData<Boolean>
    val isNextEnabled: LiveData<Boolean>
    val takePhoto: LiveData<DocItem>
    val listAdapter: List<DocItems>

    fun onNext()
    fun onTakePhotoSuccess(file: File, item: DocItem)
    fun onTakePhoto(item: DocItem)
}

class TakeDocViewModelImpl(
        private val preferences: PreferencesBasket
): ViewModelBase(), TakeDocViewModel, PermissionListener {
    override var imagePath = ""
    override val listener = this
    override val isPermissionCameraGranted = MutableLiveData(false)
    override val isNextEnabled = MutableLiveData(false)
    override val takePhoto = MutableLiveData<DocItem>()
    override val listAdapter = listOf(
        DocTitle(),
        DocText(R.string.load_sts),
        DocItem(R.string.facial_sts, FormField.STS_FRONT),
        DocItem(R.string.working_sts, FormField.STS_BACK),
        DocText(R.string.load_vy),
        DocItem(R.string.facial_vy, FormField.VU_FRONT),
        DocItem(R.string.working_vy, FormField.VU_BACK),
        DocItem(R.string.you_photo_vy, FormField.VU_SELF),
        DocText(R.string.load_passport),
        DocItem(R.string.passport_photo, FormField.PASPORT),
        DocButton(),
    )

    init {
        checkPermission(PermissionEvent(PermissionType.CAMERA))
        checkNext()
        check()
    }

    private fun check() = if (preferences.getFio() == null || preferences.getPhone() == null || preferences.getCity() == null) {
            CacheData.sid.value = null
            false
        }
        else true

    override fun onTakePhoto(item: DocItem) =
        if (isPermissionCameraGranted.value == true) {
            takePhoto.postValue(item)
            replaceFragment(FragmentEvent(FragmentType.CHOOSE_PHOTO))
        }
        else checkPermission(PermissionEvent(PermissionType.CAMERA))

    override fun onNext() {
        if (check())
        listAdapter
                .filter { it.type == DocType.ITEM }
                .map    { it as DocItem }
                .map    { it.formfield }
                .apply {
                    requestWithCallback({
                        api.sendSecond(
                            CacheData.sid.value!!, preferences.getFio()!!, preferences.getPhone()!!, preferences.getCity()!!,
                            getFileId(FormField.STS_FRONT),
                            getFileId(FormField.STS_BACK),
                            getFileId(FormField.VU_FRONT),
                            getFileId(FormField.VU_BACK),
                            getFileId(FormField.PASPORT),
                            getFileId(FormField.VU_SELF)
                        )
                    }, {
                        replaceFragment(FragmentEvent(FragmentType.INFORMATION))
                    }, { showPopUp(it) })
                }
    }

    private fun List<FormField>.getFileId(formField: FormField) = this.find { it == formField }!!.fileId!!

    private fun checkNext() =
        isNextEnabled.postValue(listAdapter
            .filter { it.type == DocType.ITEM }
            .map { it as DocItem }
            .any { it.isSuccessLoad }
        )

    override fun onTakePhotoSuccess(file: File, item: DocItem) {
        val requestFile = file.asRequestBody("image/".toMediaTypeOrNull())
        val part = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
            "files", file.name, requestFile
        ).build()
        requestWithCallback({ api.loadImage(CacheData.sid.value!!, part, item.formfield.filed) }, { loadImage ->
            if (loadImage.result.uploaded.first().id != null) {
                item.formfield.fileId = loadImage.result.uploaded.first().id
                item.filePath.postValue(loadImage.result.uploaded.first().thumb)
                checkNext()
            }
        }, {
            checkNext()
            showPopUp(it) })
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) { if (response?.permissionName == PermissionType.CAMERA.permission) isPermissionCameraGranted.postValue(
        true
    ) }
    override fun onPermissionDenied(response: PermissionDeniedResponse?) = showPopUp(R.string.access_to_camera)
    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) = token?.continuePermissionRequest() ?: Unit
}