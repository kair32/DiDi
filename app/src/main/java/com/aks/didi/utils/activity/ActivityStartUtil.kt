package com.aks.didi.utils.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.aks.didi.REQUEST_CALL
import com.aks.didi.REQUEST_TAKE_PHOTO
import com.aks.didi.utils.ActivityStartViewModel
import com.aks.didi.utils.EventBase
import com.aks.didi.utils.activity.ActivityType.*
import java.io.File

object ActivityStartUtil {
    fun observe(owner: LifecycleOwner, viewModel: ActivityStartViewModel, activity: Activity?) =
        viewModel.activityStartLiveData.observe(owner, Observer {
            if (it?.happen != false) return@Observer
            startActivity(activity ?: return@Observer, it)
        })

    private fun startActivity(activity: Activity, event: ActivityStartEvent) {
        val intent = when (event.type) {
            CALL -> Intent(Intent.ACTION_DIAL, Uri.parse("tel:$+79998887766")).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            else -> null
        }?: return
        if(intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(intent, event.type.code)
            //activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
            event.happen = true
        }
        else return
    }
}

open class ActivityStartEvent(
    val type: ActivityType,
    val id: String = "",
    val file: File? = null,
) : EventBase() {}

enum class ActivityType(val code: Int) {
    DEFAULT(0), CALL(REQUEST_CALL), TAKE_PHOTO(REQUEST_TAKE_PHOTO);
    companion object {
        fun getActivity(code: Int): ActivityType = values().find { it.code == code } ?: DEFAULT
    }
}