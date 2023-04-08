package id.rizky.anipict.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.rizky.anipict.R
import id.rizky.anipict.databinding.ItemFilterBinding

class FilterAnimalAdapter(val listener: OnClickListener) :
    RecyclerView.Adapter<FilterAnimalAdapter.FilterAdapterViewHolder>() {

    inner class FilterAdapterViewHolder constructor(private val itemBinding: ItemFilterBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClickListener(position)
                }
            }
        }

        fun bindFilter(filter: Filter) {
            itemBinding.tvItem.text = filter.name
            if (filter.isActive)
                itemBinding.tvItem.setTextColor(
                    ContextCompat.getColor(
                        itemBinding.root.context,
                        R.color.regal_blue
                    )
                )
            else
                itemBinding.tvItem.setTextColor(
                    ContextCompat.getColor(
                        itemBinding.root.context,
                        R.color.old_silver
                    )
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterAdapterViewHolder {
        val binding = ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: FilterAdapterViewHolder, position: Int) {
        val animal = differ.currentList[position]
        if (animal != null) {
            holder.bindFilter(differ.currentList[position])
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Filter>() {
        override fun areItemsTheSame(oldItem: Filter, newItem: Filter): Boolean {
            return oldItem.name == newItem.name
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Filter, newItem: Filter): Boolean {
            return oldItem == newItem
        }

    }

    interface OnClickListener {
        fun onItemClickListener(position: Int)
    }

    val differ = AsyncListDiffer(this, differCallback)

    data class Filter(val name: String, var isActive: Boolean)
}