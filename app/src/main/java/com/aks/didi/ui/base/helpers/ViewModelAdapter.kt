package com.aks.didi.ui.base.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class ViewModelAdapter<O : Any>(
    private val reverse: Boolean = false,
    private val isTopAndBottomLoadMore: Boolean = false,
    private val positionBeforeLoading: Int = 5
) : RecyclerView.Adapter<ViewHolder>() {

    protected val items = LinkedList<O>()

    private val cellMap = LinkedHashMap<Class<out O>, CellInfo>()
    private val sharedObjects = LinkedHashMap<Int, Any>()

    protected fun cell(clazz: Class<out O>, @LayoutRes layoutId: Int, bindingId: Int) {
        cellMap[clazz] = CellInfo(layoutId, bindingId)
    }
    protected fun cell(
        clazz: Class<out O>,
        @LayoutRes layoutId: Int,
        holder: Class<out RecyclerView.ViewHolder>,
        bindingId: Int
    ) {
        cellMap[clazz] = CellInfo(layoutId, bindingId, holder)
    }

    protected fun sharedObject(sharedObject: Any, bindingId: Int) {
        sharedObjects[bindingId] = sharedObject
    }

    private fun getViewModel(position: Int) = items[position]

    private fun getCellInfo(viewModel: O): CellInfo {
        cellMap.entries.find { it.key == viewModel.javaClass }
                ?.apply { return value }

        cellMap.entries.find { it.key.isInstance(viewModel) }
                ?.apply {
                    cellMap[viewModel.javaClass] = value
                    return value
                }

        throw Exception("Cell info for class ${viewModel.javaClass.name} not found.")
    }

    private fun onBind(binding: ViewDataBinding, cellInfo: CellInfo, position: Int) {
        val viewModel = getViewModel(position)
        if (cellInfo.bindingId != 0)
            binding.setVariable(cellInfo.bindingId, viewModel)
        if (isTopAndBottomLoadMore){
            if (itemCount - position == positionBeforeLoading) loadMore(true)
            if (position == positionBeforeLoading) loadMore(false)
        }
        else {
            if (reverse) {
                if (itemCount - position == positionBeforeLoading) loadMore(itemCount - 1)
            } else {
                if (position == positionBeforeLoading) loadMore(0)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return getCellInfo(getViewModel(position)).layoutId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        val viewHolder = (cellMap.entries.find { it.value.layoutId == viewType }
            ?.value?.holder?.getDeclaredConstructor(View::class.java)?.newInstance(view)
            ?: ViewHolder(view)
                ) as ViewHolder
        sharedObjects.forEach { viewHolder.binding.setVariable(it.key, it.value) }
        viewHolder.binding.setLifecycleOwner(viewHolder)
        viewHolder.initHolder()
        return viewHolder
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }
    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cellInfo = getCellInfo(getViewModel(position))
        onBind(holder.binding, cellInfo, position)
    }

    protected fun observe(liveData: LiveData<List<O>>, owner: LifecycleOwner) {
        liveData.observe(owner, androidx.lifecycle.Observer(::update))
    }

    protected open fun update(list: List<O>) {
        val callback = AdapterDiffCallback(items, list)
        update(callback, list)
    }

    protected fun update(callback: DiffUtil.Callback, list: List<O>) {
        val result = DiffUtil.calculateDiff(callback)
        items.clear()
        items.addAll(list)
        result.dispatchUpdatesTo(this)
    }

    protected open fun loadMore(position: Int) {}
    protected open fun loadMore(isTop: Boolean) {}
}

data class CellInfo(
    val layoutId: Int,
    val bindingId: Int,
    val holder: Class<out RecyclerView.ViewHolder>? = null
)

open class ViewHolder(view: View) : RecyclerView.ViewHolder(view), LifecycleOwner {
    open val binding: ViewDataBinding = DataBindingUtil.bind(view)!!
    private val lifecycleRegistry = LifecycleRegistry(this)
    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }
    fun markDetach() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)//on the test
    }
    fun markAttach() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)//on the test
    }
    override fun getLifecycle(): Lifecycle = lifecycleRegistry
    open fun initHolder(){}
}

open class AdapterDiffCallback<O>(private val oldList: List<O>, private val newList: List<O>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
            = oldList[oldItemPosition] === newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean
            = oldList[oldItemPosition] == newList[newItemPosition]
}