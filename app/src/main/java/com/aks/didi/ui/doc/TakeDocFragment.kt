package com.aks.didi.ui.doc

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aks.didi.BuildConfig
import com.aks.didi.databinding.FragmentDocBinding
import com.aks.didi.ui.photo.TakePhotoViewModelImpl
import com.aks.didi.utils.activity.ActivityType
import com.aks.didi.utils.fragment.FragmentUtil
import com.aks.didi.utils.shared.SharedUtil
import java.io.File
import java.util.*

class TakeDocFragment: Fragment() {
    private val fragmentUtil = FragmentUtil
    private val sharedUtil = SharedUtil

    private lateinit var viewModel: TakeDocViewModel
    override fun onAttach(context: Context) {
        super.onAttach(context)
        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(this).get(TakeDocViewModelImpl::class.java)
        fragmentUtil.observe(this, viewModel, activity)
        sharedUtil.observe(this, viewModel, activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentDocBinding.inflate(inflater, container, false)

        binding.rv.adapter = DocAdapter(viewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        takePhoto()
        return binding.root
    }

    private var photoFile: File? = null
    private val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    private fun takePhoto(){
        photoFile = createImageFile("jpg")
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(photoFile!!))
        val activities = context!!.packageManager.queryIntentActivities(photoIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (activity in activities)
            context!!.grantUriPermission(
                    activity.activityInfo.packageName,
                    getUriForFile(photoFile!!),
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(photoIntent, ActivityType.TAKE_PHOTO.code)
        viewModel.imagePath = photoFile?.absolutePath?:""
    }

    private fun getUriForFile(file: File) = FileProvider.getUriForFile(activity!!, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
    private fun createImageFile(extension: String): File = File(imageFileDestination() + "/" + imageFileName(extension))
    private fun imageFileDestination() = context!!.cacheDir.absolutePath
    private fun imageFileName(extension: String) = "IMG_${java.lang.Long.toString(Date().time, 16)}.$extension"

    companion object {
        fun newInstance() = TakeDocFragment()
    }
}