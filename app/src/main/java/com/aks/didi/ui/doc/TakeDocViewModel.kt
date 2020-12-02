package com.aks.didi.ui.doc

import com.aks.didi.ui.base.viewmodel.ViewModelBase
import com.aks.didi.utils.FragmentViewModel
import com.aks.didi.utils.SharedViewModel

interface TakeDocViewModel: FragmentViewModel, SharedViewModel {
    var imagePath: String
    fun onTakePhoto()
}

class TakeDocViewModelImpl: ViewModelBase(), TakeDocViewModel{
    override var imagePath = ""
    override fun onTakePhoto() {}
}