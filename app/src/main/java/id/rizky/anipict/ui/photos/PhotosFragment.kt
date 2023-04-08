package id.rizky.anipict.ui.photos

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.rizky.anipict.R
import id.rizky.anipict.data.model.Photo
import id.rizky.anipict.databinding.FragmentPhotosBinding
import id.rizky.anipict.ui.photos.adapter.PhotoAdapter
import id.rizky.anipict.ui.photos.adapter.PhotoLoadStateAdapter
import id.rizky.anipict.utils.FilterAnimalAdapter
import id.rizky.anipict.utils.SpacingItemDecoration
import id.rizky.anipict.utils.dpToPx
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PhotosFragment : Fragment(R.layout.fragment_photos), PhotoAdapter.OnItemClickListener,
    FilterAnimalAdapter.OnClickListener {

    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!

    private val photosAdapter: PhotoAdapter by lazy { PhotoAdapter(this) }
    private val filterAnimalAdapter: FilterAnimalAdapter by lazy { FilterAnimalAdapter(this) }

    private val args: PhotosFragmentArgs by navArgs()

    @Inject
    internal lateinit var photosViewModelFactory: PhotosViewModel.AssistedFactory

    private val viewModel: PhotosViewModel by viewModels {
        PhotosViewModel.provideFactory(
            photosViewModelFactory,
            args.query
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPhotosBinding.bind(view)

        setupToolbar()
        setupFilterAdapter()
        setupPhotosAdapter()
        photosLoadStateListener()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbarTitle.text = args.query
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupFilterAdapter() {
        binding.rvFilter.adapter = filterAnimalAdapter
    }

    private fun setupPhotosAdapter() {
        val headerAdapter = PhotoLoadStateAdapter { photosAdapter.retry() }
        val footerAdapter = PhotoLoadStateAdapter { photosAdapter.retry() }
        val concatAdapter = photosAdapter.withLoadStateHeaderAndFooter(
            header = headerAdapter,
            footer = footerAdapter,
        )
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        binding.apply {
            rvPhoto.layoutManager = staggeredGridLayoutManager
            rvPhoto.setHasFixedSize(true)
            (rvPhoto.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            rvPhoto.adapter = concatAdapter
            rvPhoto.addItemDecoration(SpacingItemDecoration(16.dpToPx(requireContext())))
            btnRetry.setOnClickListener {
                viewModel.animalFlow.retry()
                photosAdapter.retry()
            }
        }
    }

    private fun photosLoadStateListener() {
        photosAdapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                rvPhoto.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading
                progressCircularLoading.isVisible =
                    combinedLoadStates.source.refresh is LoadState.Loading
                btnRetry.isVisible = combinedLoadStates.source.refresh is LoadState.Error
                tvErrorMessage.isVisible = combinedLoadStates.source.refresh is LoadState.Error
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.photosFlow.collectLatest {
                        photosAdapter.submitData(it)
                    }
                }

                launch {
                    viewModel.filterData.collectLatest {
                        filterAnimalAdapter.differ.submitList(it)
                    }
                }

                launch {
                    viewModel.errorAnimalChannel.collectLatest {
                        binding.btnRetry.isVisible = it
                        binding.tvErrorMessage.isVisible = it
                    }
                }

                launch {
                    viewModel.loadingAnimalChannel.collectLatest {
                        binding.progressCircularLoading.isVisible = it
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickPhotoOrFavoriteButton(photo: Photo) {
        viewModel.insertOrDeleteFavorite(photo)
        if (photo.liked)
            Snackbar.make(
                binding.root,
                getString(R.string.success_msg_delete_photo_from_favorite),
                Snackbar.LENGTH_SHORT
            ).show()
        else
            Snackbar.make(
                binding.root,
                getString(R.string.success_msg_insert_photo_to_favorite),
                Snackbar.LENGTH_SHORT
            ).show()
    }

    override fun onItemClickListener(position: Int) {
        viewModel.applyFilter(position)

        (binding.rvPhoto.layoutManager as StaggeredGridLayoutManager)
            .scrollToPositionWithOffset(0, 0)
    }
}