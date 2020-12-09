package com.aks.didi.ui.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aks.didi.databinding.FragmentInformationBinding
import com.aks.didi.model.CacheData

class InformationFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInformationBinding.inflate(inflater, container, false)

        binding.bt.setOnClickListener { CacheData.sid.postValue(null) }
        
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    companion object {
        fun newInstance() = InformationFragment()
    }
}