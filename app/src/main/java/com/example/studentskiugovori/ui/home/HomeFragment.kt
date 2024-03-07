package com.example.studentskiugovori.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
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

        val sharedPref : SharedPreferences by KoinJavaComponent.inject(SharedPreferences::class.java)

        val composeView = binding.composeView
        binding.composeView.setBackgroundColor(resources.getColor(R.color.md_theme_background))
        homeViewModel.getData()

        homeViewModel.ugovori.observe(viewLifecycleOwner) {
            composeView.setContent {
                HomeCompose(homeViewModel)
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