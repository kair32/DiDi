package com.aks.didi.ui.photo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aks.didi.databinding.FragmentTakePhoneBinding
import com.aks.didi.ui.base.helpers.setFocus
import com.aks.didi.utils.fragment.FragmentUtil
import com.aks.didi.utils.shared.SharedUtil

class TakePhotoFragment: Fragment() {
    private val fragmentUtil = FragmentUtil
    private val sharedUtil = SharedUtil

    private lateinit var viewModel: TakePhotoViewModel
    override fun onAttach(context: Context) {
        super.onAttach(context)
        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(this).get(TakePhotoViewModelImpl::class.java)
        fragmentUtil.observe(this, viewModel, activity)
        sharedUtil.observe(this, viewModel, activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTakePhoneBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        binding.etCity.setOnFocusChangeListener { _, b -> viewModel.isFocusCity.postValue(b) }
        viewModel.isFocusCity.observe(viewLifecycleOwner){ if( it == false ) clearFocus(binding.etCity) }
        binding.rv.adapter = CityAdapter(viewModel, viewLifecycleOwner)

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun clearFocus(et: EditText){
        et.clearFocus()
        val imm = et.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(et.windowToken?: return, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    companion object {
        fun newInstance() = TakePhotoFragment()
    }
}