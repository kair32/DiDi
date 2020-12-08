package com.aks.didi.ui.doc

import androidx.lifecycle.MutableLiveData
import com.aks.didi.BR
import com.aks.didi.R
import com.aks.didi.ui.base.helpers.ViewModelAdapter
import java.io.File

interface DocItems{
    val type: DocType
}

class DocItem(val title: Int, val formfield: FormField): DocItems{
    val filePath = MutableLiveData<File>()
    override val type: DocType = DocType.ITEM
}
class DocText(val text: Int, override val type: DocType = DocType.TEXT): DocItems
class DocTitle(override val type: DocType = DocType.TITLE): DocItems
class DocButton(override val type: DocType = DocType.BUTTON): DocItems

class DocAdapter(viewModel: TakeDocViewModel): ViewModelAdapter<DocItems>() {
    init {
        sharedObject(viewModel, BR.viewModel)
        cell(DocTitle::class.java, R.layout.item_doc_title, BR.item)
        cell(DocText::class.java, R.layout.item_text_doc, BR.item)
        cell(DocItem::class.java, R.layout.item_doc, BR.item)
        cell(DocButton::class.java, R.layout.item_doc_button, BR.item)
        update(viewModel.listAdapter)
    }

    fun getItem(item: DocItem) = items.find { it == item}
}

enum class FormField(val filed: String, var fileId: String? = null){
    STS_FRONT("sts_front"),
    STS_BACK("sts_back"),
    VU_FRONT("vu_front"),
    VU_BACK("vu_back"),
    VU_SELF("vu_self"),
    PASPORT("pasport"),
}

enum class DocType {
    ITEM,
    TEXT,
    TITLE,
    BUTTON
}