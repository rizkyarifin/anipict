package id.rizky.anipict.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.rizky.anipict.R
import id.rizky.anipict.databinding.FragmentHomeBinding
import id.rizky.anipict.ui.home.adapter.AnimalAdapter
import id.rizky.anipict.utils.SpacingItemDecoration
import id.rizky.anipict.utils.dpToPx
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), AnimalAdapter.OnClickListener {

    private val animalAdapter by lazy {
        AnimalAdapter(this)
    }
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupAdapter()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.animalDataFlow.collectLatest {
                    animalAdapter.differ.submitList(it)
                }
            }
        }
    }

    private fun setupAdapter() {
        binding.rvAnimals.adapter = animalAdapter
        binding.rvAnimals.addItemDecoration(SpacingItemDecoration(16.dpToPx(requireContext())))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClickListener(animal: String) {
        val action = HomeFragmentDirections.actionNavigationHomeToPhotosFragment(animal)
        findNavController().navigate(action)
    }
}