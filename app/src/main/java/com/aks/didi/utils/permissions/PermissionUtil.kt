package com.aks.didi.utils.permissions

import android.Manifest
import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.aks.didi.utils.EventBase
import com.aks.didi.utils.PermissionViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

object PermissionUtil {
    fun observe(owner: LifecycleOwner, viewModel: PermissionViewModel, activity: Activity?) =
        viewModel.permissionLiveData.observe(owner, Observer {
            if (it?.happen != false) return@Observer
            if (it.listType == null)    checkPermission(activity ?: return@Observer, viewModel.listener, it)
            it.happen = true
        })

    private fun checkPermission(activity: Activity, listener: PermissionListener, event: PermissionEvent) =
        Dexter.withActivity(activity)
            .withPermission(event.type!!.permission)
            .withListener(listener)
            .check()
}

class PermissionEvent: EventBase {
    constructor(type: PermissionType): super(){
        this.type = type }
    constructor(listType: List<PermissionType>): super(){
        this.listType = listType }

    var type: PermissionType?=null
    var listType: List<PermissionType>?=null
}

enum class PermissionType(val permission: String) {
    READ_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
    CAMERA(Manifest.permission.CAMERA)
}