package com.aks.didi.ui.base.helpers

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.BindingAdapter
import coil.load
import coil.size.Scale
import com.aks.didi.R
import com.aks.didi.ui.photo.TakePhotoViewModel
import com.redmadrobot.inputmask.MaskedTextChangedListener
import java.io.File

@BindingAdapter("isVisible")
fun setVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("textBin")
fun setTextBin(view: TextView, res: Int?) = if (res!=null && res != 0) view.setText(res) else Unit

@BindingAdapter("setFocus")
fun setFocus(et: EditText, isFocus: Boolean?){
    if (isFocus == true) {
        Handler(Looper.getMainLooper()).postDelayed({
            et.requestFocus()
            et.isFocusableInTouchMode = true
            et.setSelection(et.text.length)
            val imm = et.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }, 1)
    }
    if (isFocus == false){
        et.clearFocus()
        val imm = et.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(et.windowToken?: return, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}

@BindingAdapter("transitionManager")
fun setTransition(view: View, ignor: Boolean) {
    try { val viewGroup = view.rootView as ViewGroup
        TransitionManager.beginDelayedTransition(viewGroup)
    } catch (ignored: NullPointerException) { }
}

@BindingAdapter("phoneEditText")
fun initTextChangedListener(et: EditText, viewModel: TakePhotoViewModel?){
    val listener = object : MaskedTextChangedListener("+7 [000] [000]-[00]-[00]",
        false, et, null,
        object : ValueListener {
            override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
                val text = et.text.toString()
                if (extractedValue == "" && (text == "+7"))
                    return et.setText("+7 ")
                viewModel?.phone?.postValue(extractedValue)
            }
        }) {
        override fun onFocusChange(view: View?, hasFocus: Boolean) {
            super.onFocusChange(view, hasFocus)
            val text = et.text.toString()
            if (hasFocus && text == "") et.setText("+7 ")
            else if (!hasFocus && text == "+7 ") et.setText("")
        }
    }
    et.addTextChangedListener(listener)
    et.onFocusChangeListener = listener
}

@BindingAdapter("topTopParentConstraint")
fun setTopTopParentConstraint(view: View, id: Int) {
    val newId = if (id == 0) ConstraintSet.PARENT_ID else id
    val layout = view.parent as ConstraintLayout
    ConstraintSet().apply {
        clone(layout)
        clear(view.id, ConstraintSet.TOP)
        connect(view.id, ConstraintSet.TOP, newId,
            if (id != 0) ConstraintSet.BOTTOM else ConstraintSet.TOP,
            view.resources.getDimensionPixelOffset(R.dimen.margin_20))
        applyTo(layout)
    }
}

@BindingAdapter("loadImage")
fun setLoadImage(iv: ImageView, path: File?){
    iv.load(path){
        scale(Scale.FILL)
    }
}