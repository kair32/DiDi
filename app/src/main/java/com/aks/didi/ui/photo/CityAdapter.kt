package com.aks.didi.ui.photo

import androidx.lifecycle.LifecycleOwner
import com.aks.didi.R
import com.aks.didi.BR
import com.aks.didi.ui.base.helpers.AdapterDiffCallback
import com.aks.didi.ui.base.helpers.ViewModelAdapter

class CityItem(val city: String)

class CityAdapter(viewModel: TakePhotoViewModel, owner: LifecycleOwner): ViewModelAdapter<CityItem>() {
    init {
        sharedObject(viewModel, BR.viewModel)
        cell(CityItem::class.java, R.layout.item_city, BR.item)
        viewModel.cities.observe(owner){cities -> update(cities.map { CityItem(it) })}
    }

    override fun update(list: List<CityItem>) {
        val callback = CityDiffCallback(items, list)
        update(callback, list)
    }
}

class CityDiffCallback(
    private val oldList: List<CityItem>,
    private val newList: List<CityItem>
): AdapterDiffCallback<CityItem>(oldList, newList) {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.city == newItem.city
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
        = oldList[oldItemPosition] === newList[newItemPosition]
}