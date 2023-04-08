package id.rizky.anipict.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.rizky.anipict.databinding.ItemAnimalsBinding
import id.rizky.anipict.utils.loadPhotoFromResource

class AnimalAdapter(
    private val listener: OnClickListener
) :
    RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

    inner class AnimalViewHolder(private val binding: ItemAnimalsBinding) :
        ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                setOnItemClick(bindingAdapterPosition)
            }
            binding.btnBrowsePicture.setOnClickListener {
                setOnItemClick(bindingAdapterPosition)
            }
        }

        fun bindAnimal(animal: Animal) {
            binding.imgAnimal.loadPhotoFromResource(animal.image)
            binding.tvAnimalName.text = animal.name
            binding.tvAnimalGroup.text = animal.group
        }
    }

    private fun setOnItemClick(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            listener.onItemClickListener(differ.currentList[position].name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val binding =
            ItemAnimalsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimalViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = differ.currentList[position]
        if (animal != null) {
            holder.bindAnimal(differ.currentList[position])
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Animal>() {
        override fun areItemsTheSame(oldItem: Animal, newItem: Animal): Boolean {
            return oldItem.name == newItem.name
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Animal, newItem: Animal): Boolean {
            return oldItem == newItem
        }

    }


    interface OnClickListener {
        fun onItemClickListener(animal: String)
    }

    val differ = AsyncListDiffer(this, differCallback)

    data class Animal(val name: String, val group: String, val image: Int)
}