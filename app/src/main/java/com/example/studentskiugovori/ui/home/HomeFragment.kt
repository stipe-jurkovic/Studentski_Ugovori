package com.example.studentskiugovori.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.studentskiugovori.compose.AppTheme
import com.example.studentskiugovori.R
import com.example.studentskiugovori.databinding.FragmentHomeBinding
import com.example.studomatisvu.compose.HomeCompose
import org.koin.java.KoinJavaComponent

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel: HomeViewModel by KoinJavaComponent.inject(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val composeView = binding.composeView
        binding.composeView.setBackgroundColor(resources.getColor(R.color.md_theme_background))

        homeViewModel.getData()

        homeViewModel.ugovori.observe(viewLifecycleOwner) {
            composeView.setContent {
                AppTheme(){ HomeCompose(homeViewModel) }
            }
            composeView.setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                    viewLifecycleOwner
                )
            )
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}