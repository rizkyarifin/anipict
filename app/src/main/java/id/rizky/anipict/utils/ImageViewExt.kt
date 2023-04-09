package id.rizky.anipict.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewbinding.ViewBinding
import id.rizky.anipict.GlideApp
import id.rizky.anipict.data.model.Photo

fun ImageView.loadGridPhotoUrl(
    url: String,
    color: String?,
) {
    color?.let { background = ColorDrawable(Color.parseColor(it)) }
    GlideApp.with(context)
        .load(url)
        .into(this)
}

fun ImageView.loadPhotoFromResource(
    resource: Int,
) {
    GlideApp.with(context)
        .load(resource)
        .into(this)
}

fun calculateImageDimensionRatio(photo: Photo): String {
    return if (photo.width.toFloat() / photo.height.toFloat() > 1.8) {
        String.format("4000:3000")
    } else {
        String.format("%d:%d", photo.width, photo.height)
    }
}

fun setImageDimensionRatio(
    binding: ViewBinding,
    ratio: String,
    parentItemPhotoConstraint: ConstraintLayout,
    itemImageView: ImageView
) {
    val set = ConstraintSet()
    binding.apply {
        set.clone(parentItemPhotoConstraint)
        set.setDimensionRatio(itemImageView.id, ratio)
        set.applyTo(parentItemPhotoConstraint)
    }
}