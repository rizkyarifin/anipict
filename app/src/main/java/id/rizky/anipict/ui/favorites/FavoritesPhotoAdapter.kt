package id.rizky.anipict.ui.favorites

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.rizky.anipict.data.model.Photo
import id.rizky.anipict.databinding.ItemPhotoFavouritesBinding
import id.rizky.anipict.utils.calculateImageDimensionRatio
import id.rizky.anipict.utils.loadGridPhotoUrl
import id.rizky.anipict.utils.setImageDimensionRatio

class FavoritesPhotoAdapter(
    private val listener: OnClickListener
) :
    RecyclerView.Adapter<FavoritesPhotoAdapter.FavouritePhotoViewHolder>() {

    inner class FavouritePhotoViewHolder constructor(
        private val binding: ItemPhotoFavouritesBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.itemFavouriteBtn.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFavoriteButtonClick(differ.currentList[position], position)
                }
            }
        }

        fun bindPhoto(photo: Photo) = with(binding) {
            itemImageView.loadGridPhotoUrl(photo.src.large, photo.avg_color)
            tvAnimalName.text = photo.animal
            setImageDimensionRatio(
                binding,
                calculateImageDimensionRatio(photo),
                parentItemPhotoConstraint,
                itemImageView,
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritePhotoViewHolder {
        val binding =
            ItemPhotoFavouritesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouritePhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouritePhotoViewHolder, position: Int) {
        val photo = differ.currentList[position]
        if (photo != null) {
            holder.bindPhoto(differ.currentList[position])
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }

    }

    interface OnClickListener {
        fun onFavoriteButtonClick(photo: Photo, position: Int)
    }

    val differ = AsyncListDiffer(this, differCallback)
}