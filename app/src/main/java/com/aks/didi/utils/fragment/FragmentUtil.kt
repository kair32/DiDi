package com.aks.didi.utils.fragment

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aks.didi.R
import com.aks.didi.ui.base.viewmodel.ViewModelBase
import com.aks.didi.ui.doc.ChoosePhoto
import com.aks.didi.ui.doc.TakeDocFragment
import com.aks.didi.ui.information.InformationFragment
import com.aks.didi.ui.photo.TakePhotoFragment
import com.aks.didi.utils.EventBase
import com.aks.didi.utils.FragmentViewModel
import com.aks.didi.utils.fragment.FragmentType.*

object FragmentUtil {
    fun observe(owner: LifecycleOwner, viewModel: FragmentViewModel, activity: FragmentActivity?,
                consumer: (FragmentEvent) -> Unit = {}) {
        // М.б. subject не нужен и можно просто вызывать consumer напрямую?
        viewModel.fragmentLiveData.observe(owner, Observer {
            if (it?.happen != false) return@Observer; activity ?: return@Observer
            if (it.isRemove) removeFragment(activity.supportFragmentManager, it)
            else replaceFragment(activity.supportFragmentManager, it)
        })
    }

    private fun replaceFragment(manager: FragmentManager, event: FragmentEvent) {
        val transaction = manager.beginTransaction()
        val fragment = createFragment(event)
        if (fragment is DialogFragment) return fragment.show(manager, event.type.name)
        when(event.type.animation){
            AnimationType.RIGHT_TO_LEFT-> {
                if (event.isBack) transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                else transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            else -> {}
        }

        transaction.replace(event.type.id, fragment, event.type.name)
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        transaction.commit()
        event.happen = true
    }

    private fun createFragment(event: FragmentEvent) = when (event.type) {
        TAKE_PHONE      -> TakePhotoFragment.newInstance()
        TAKE_DOC        -> TakeDocFragment.newInstance()
        CHOOSE_PHOTO    -> ChoosePhoto.newInstance()
        INFORMATION     -> InformationFragment.newInstance()
    }

    private fun removeFragment(manager: FragmentManager, event: FragmentEvent) {
        val fragment = manager.findFragmentByTag(event.type.name) ?: return
        manager.beginTransaction().remove(fragment).commit()
    }
}

inline fun <reified T: ViewModelBase> getViewModelFragment(activity: FragmentActivity?, vararg tags :String?): T?{
    tags.map { tag ->
        activity?.supportFragmentManager?.findFragmentByTag(tag)?.let {
            return ViewModelProvider(it).get(T::class.java)
        }
    }
    return null
}

fun getFragment(activity: FragmentActivity?, vararg tags :String?): Fragment?{
    tags.map { tag ->
        activity?.supportFragmentManager?.findFragmentByTag(tag)?.let {
            return it
        }
    }
    return null
}

open class FragmentEvent(
    val type: FragmentType,
    val isRemove: Boolean = false,
    val isBack: Boolean = false
) : EventBase()

enum class FragmentType(
    val id: Int = R.id.fragment_container,
    val animation:AnimationType = AnimationType.RIGHT_TO_LEFT){

    TAKE_PHONE,
    TAKE_DOC,
    CHOOSE_PHOTO,
    INFORMATION
}
enum class AnimationType{
    NONE,
    RIGHT_TO_LEFT
}