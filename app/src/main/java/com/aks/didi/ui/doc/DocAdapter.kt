package com.aks.didi.ui.doc

import com.aks.didi.BR
import com.aks.didi.R
import com.aks.didi.ui.base.helpers.ViewModelAdapter

interface DocItems

class DocItem(val title: Int): DocItems
class DocText(val text: Int): DocItems

class DocAdapter(viewModel: TakeDocViewModel): ViewModelAdapter<DocItems>() {
    init {
        sharedObject(viewModel, BR.viewModel)
        cell(DocText::class.java, R.layout.item_text_doc, BR.item)
        cell(DocItem::class.java, R.layout.item_doc, BR.item)
        update(listOf(
                DocText(R.string.load_sts),
                DocItem(R.string.facial_sts),
                DocItem(R.string.working_sts),
                DocText(R.string.load_vy),
                DocItem(R.string.facial_vy),
                DocItem(R.string.working_vy),
                DocItem(R.string.you_photo_vy),
                DocText(R.string.load_passport),
                DocItem(R.string.passport_photo),
        ))
    }
}