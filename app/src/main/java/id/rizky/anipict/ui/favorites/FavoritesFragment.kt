package id.rizky.anipict.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.rizky.anipict.R
import id.rizky.anipict.data.model.Photo
import id.rizky.anipict.databinding.FragmentFavoriteBinding
import id.rizky.anipict.ui.favorites.adapter.FavoritesPhotoAdapter
import id.rizky.anipict.utils.FilterAnimalAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorite),
    FavoritesPhotoAdapter.OnClickListener, FilterAnimalAdapter.OnClickListener {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels()
    private val filterAnimalAdapter: FilterAnimalAdapter by lazy { FilterAnimalAdapter(this) }
    private val favouritesPhotoAdapter: FavoritesPhotoAdapter by lazy { FavoritesPhotoAdapter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)

        setupAdapter()
        observeViewModel()
    }

    private fun setupAdapter() {
        binding.rvFavorite.adapter = favouritesPhotoAdapter
        binding.rvFilter.adapter = filterAnimalAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.favoritePhotosFlow.collectLatest {
                        showEmptyState(it)
                        favouritesPhotoAdapter.differ.submitList(it)
                    }
                }

                launch {
                    viewModel.filterDataFlow.collectLatest {
                        filterAnimalAdapter.differ.submitList(it)
                    }
                }
            }
        }
    }

    private fun showEmptyState(listPhoto : List<Photo>) {
        if (listPhoto.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvFavorite.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvFavorite.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFavoriteButtonClick(photo: Photo, position: Int) {
        viewModel.deletePhotoFromFavorite(photo.id)

        Snackbar.make(
            binding.root,
            getString(R.string.success_msg_delete_photo_from_favorite),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onItemClickListener(position: Int) {
        (binding.rvFavorite.layoutManager as LinearLayoutManager)
            .scrollToPositionWithOffset(0, 0)
        viewModel.applyFilter(position)
    }
}