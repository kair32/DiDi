package com.aks.didi.ui.doc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aks.didi.databinding.FragmentChoosePhotoBinding
import com.aks.didi.utils.fragment.FragmentType
import com.aks.didi.utils.fragment.getFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChoosePhoto(): BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentChoosePhotoBinding.inflate(inflater, container, false)
        val sharedFragment: TakeDocFragment = getFragment(activity, FragmentType.TAKE_DOC.name) as TakeDocFragment
        binding.tvPickPhoto.setOnClickListener {
            sharedFragment.pickImage()
            dismiss()
        }
        binding.tvTakePhoto.setOnClickListener {
            sharedFragment.takePhoto()
            dismiss()
        }
        return binding.root
    }

    companion object {
        fun newInstance() = ChoosePhoto()
    }
}