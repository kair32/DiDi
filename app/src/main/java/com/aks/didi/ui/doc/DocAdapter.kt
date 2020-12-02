package com.aks.didi.ui.doc

import androidx.lifecycle.MutableLiveData
import com.aks.didi.BR
import com.aks.didi.R
import com.aks.didi.ui.base.helpers.ViewModelAdapter
import java.io.File

interface DocItems

class DocItem(val title: Int): DocItems{
    val filePath = MutableLiveData<File>()
}
class DocText(val text: Int): DocItems
class DocTitle: DocItems
class DocButton: DocItems

class DocAdapter(viewModel: TakeDocViewModel): ViewModelAdapter<DocItems>() {
    init {
        sharedObject(viewModel, BR.viewModel)
        cell(DocTitle::class.java, R.layout.item_doc_title, BR.item)
        cell(DocText::class.java, R.layout.item_text_doc, BR.item)
        cell(DocItem::class.java, R.layout.item_doc, BR.item)
        cell(DocButton::class.java, R.layout.item_doc_button, BR.item)
        update(listOf(
            DocTitle(),
            DocText(R.string.load_sts),
            DocItem(R.string.facial_sts),
            DocItem(R.string.working_sts),
            DocText(R.string.load_vy),
            DocItem(R.string.facial_vy),
            DocItem(R.string.working_vy),
            DocItem(R.string.you_photo_vy),
            DocText(R.string.load_passport),
            DocItem(R.string.passport_photo),
            DocButton(),
        ))
    }

    fun getItem(item: DocItem) = items.find { it == item}
}