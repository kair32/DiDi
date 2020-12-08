package com.aks.didi.ui.doc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aks.didi.BuildConfig
import com.aks.didi.databinding.FragmentDocBinding
import com.aks.didi.ui.photo.TakePhotoViewModelImpl
import com.aks.didi.utils.activity.ActivityType
import com.aks.didi.utils.fragment.FragmentUtil
import com.aks.didi.utils.permissions.PermissionUtil
import com.aks.didi.utils.shared.SharedUtil
import me.shaohui.advancedluban.OnCompressListener
import java.io.File
import java.util.*

class TakeDocFragment: Fragment(), OnCompressListener {
    private val fragmentUtil = FragmentUtil
    private val sharedUtil = SharedUtil
    private val permissionUtil = PermissionUtil

    private lateinit var viewModel: TakeDocViewModel
    private var item: DocItem? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(this).get(TakeDocViewModelImpl::class.java)
        fragmentUtil.observe(this, viewModel, activity)
        sharedUtil.observe(this, viewModel, activity)
        permissionUtil.observe(this, viewModel, activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentDocBinding.inflate(inflater, container, false)

        binding.rv.adapter = DocAdapter(viewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.takePhoto.observe(viewLifecycleOwner){
            if (it != null)
                item = (binding.rv.adapter as DocAdapter).getItem(it)?.let { it as DocItem }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when(ActivityType.getActivity(requestCode)) {
            ActivityType.TAKE_PHOTO -> {
                photoFile?.let {
                    if (it.exists()) {
                        context!!.revokeUriPermission(getUriForFile(it), Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        luban(it)
                    }
                }
            }
            ActivityType.PICK_PHOTO -> getPathFromData(data)
            else -> return
        }
    }

    private fun getPathFromData(data: Intent?) {
       /* data ?: return
        var realPath = String()
        val uri = data.data
        data.data?.let { path ->

            var databaseUri: Uri
            val selection: String?
            val selectionArgs: Array<String>?
            if (path.contains("/document/image:")) { // files selected from "Documents"
                databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                selection = "_id=?"
                selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
            } else { // files selected from all other sources, especially on Samsung devices
                //databaseUri = uri
                selection = null
                selectionArgs = null
            }
            try {
                val column = "_data"
                val projection = arrayOf(column)
                val cursor = context?.contentResolver?.query(
                        databaseUri,
                        projection,
                        selection,
                        selectionArgs,
                        null
                )
                cursor?.let {
                    if (it.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(column)
                        realPath = cursor.getString(columnIndex)
                    }
                    cursor.close()
                }
            } catch (e: Exception) {
                println(e)
            }
        }
        item?.filePath?.value = File(realPath)
        luban(File(realPath))*/
        /*data ?: return

        val image = data.data ?: return
        val path: String = activity!!.contentResolver?.query(image, null, null, null, null)?.use {
            it.moveToFirst()
            val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return@use if (idx >= 0) it.getString(idx) else null
        } ?: image.path ?: return
        item?.filePath?.value = File(path)
        luban(File(path))*/
    }

    private fun luban(file: File) =
        me.shaohui.advancedluban.Luban.compress(activity, file)
            //.setMaxSize(199990)
            .setMaxSize(5000)
            .putGear(me.shaohui.advancedluban.Luban.CUSTOM_GEAR)
            .launch(this)

    override fun onSuccess(file: File?) {
        if (file!=null) {
            item?.filePath?.value = file
            viewModel.imagePath = file.path
            viewModel.onTakePhotoSuccess(file)
        }
    }

    override fun onError(e: Throwable?) {
        Log.d("","")
    }

    private var photoFile: File? = null
    private val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    fun takePhoto(){
        photoFile = createImageFile("jpg")
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(photoFile!!))
        val activities = context!!.packageManager.queryIntentActivities(photoIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (activity in activities)
            context!!.grantUriPermission(
                    activity.activityInfo.packageName,
                    getUriForFile(photoFile!!),
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(photoIntent, ActivityType.TAKE_PHOTO.code)
        //viewModel.imagePath = photoFile?.absolutePath?:""
    }

    fun pickImage() {
        if (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(intent, ActivityType.PICK_PHOTO.code)
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    private fun getUriForFile(file: File) = FileProvider.getUriForFile(activity!!, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
    private fun createImageFile(extension: String): File = File(imageFileDestination() + "/" + imageFileName(extension))
    private fun imageFileDestination() = context!!.cacheDir.absolutePath
    private fun imageFileName(extension: String) = "IMG_${java.lang.Long.toString(Date().time, 16)}.$extension"

    companion object {
        fun newInstance() = TakeDocFragment()
    }
}