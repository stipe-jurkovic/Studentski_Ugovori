package com.ugovori.studentskiugovori.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.ugovori.studentskiugovori.MainViewModel
import com.ugovori.studentskiugovori.compose.AppTheme
import com.ugovori.studentskiugovori.R
import com.ugovori.studentskiugovori.databinding.FragmentHomeBinding
import com.ugovori.studomatisvu.compose.HomeCompose
import org.koin.java.KoinJavaComponent

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by KoinJavaComponent.inject(MainViewModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val composeView = binding.composeView
        binding.composeView.setBackgroundColor(resources.getColor(R.color.md_theme_background))

        mainViewModel.ugovori.observe(viewLifecycleOwner) {
            composeView.setContent {
                AppTheme(){ HomeCompose(mainViewModel) }
            }
            composeView.setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                    viewLifecycleOwner
                )
            )
        }

        return binding.root
    }

    override fun onResume() {
        mainViewModel.getData()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}