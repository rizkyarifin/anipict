package id.rizky.anipict.ui.photos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.rizky.anipict.R
import id.rizky.anipict.data.model.Photo
import id.rizky.anipict.databinding.ItemPhotoBinding
import id.rizky.anipict.utils.calculateImageDimensionRatio
import id.rizky.anipict.utils.loadGridPhotoUrl
import id.rizky.anipict.utils.setImageDimensionRatio

class PhotoAdapter(
    private val listener: OnClickListener
) : PagingDataAdapter<Photo, PhotoAdapter.GalleryViewHolder>(PhotoDiffCallback()) {

    inner class GalleryViewHolder(
        private val binding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.parentItemPhotoConstraint.setOnClickListener {
                onItemLiked(getItem(bindingAdapterPosition), bindingAdapterPosition)
            }
            binding.itemFavouriteBtn.setOnClickListener {
                onItemLiked(getItem(bindingAdapterPosition), bindingAdapterPosition)
            }
        }

        fun bind(photo: Photo) = with(binding) {
            itemImageView.loadGridPhotoUrl(photo.src.medium, photo.avg_color)
            if (photo.liked)
                itemFavouriteBtn.setIconResource(R.drawable.ic_favorite_active_24dp)
            else
                itemFavouriteBtn.setIconResource(R.drawable.ic_favorite_in_active_24dp)
            setImageDimensionRatio(
                binding,
                calculateImageDimensionRatio(photo),
                parentItemPhotoConstraint,
                itemImageView,
            )
        }
    }

    private fun onItemLiked(item: Photo?, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            if (item != null) {
                listener.onClickPhotoOrFavoriteButton(item)
                item.liked = !item.liked
                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val photo = getItem(position)
        if (photo != null) {
            holder.bind(photo)
        }
    }

    interface OnClickListener {
        fun onClickPhotoOrFavoriteButton(photo: Photo)
    }

    private class PhotoDiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Photo, newItem: Photo) = oldItem == newItem
    }
}