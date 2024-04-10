package com.example.studentskiugovori.ui.fulllist

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.studentskiugovori.MainViewModel
import com.example.studentskiugovori.compose.AppTheme
import com.example.studentskiugovori.databinding.FragmentFulllistBinding
import org.koin.java.KoinJavaComponent

class FullListFragment : Fragment() {

    private var _binding: FragmentFulllistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val mainViewModel: MainViewModel by KoinJavaComponent.inject(MainViewModel::class.java)

        _binding = FragmentFulllistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPref : SharedPreferences by KoinJavaComponent.inject(SharedPreferences::class.java)


        val composeView = binding.composeView
        //homeViewModel.getData()

        mainViewModel.ugovori.observe(viewLifecycleOwner) {
            composeView.setContent {
                AppTheme(){ FullListCompose(mainViewModel) }
            }
            composeView.setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                    viewLifecycleOwner
                )
            )
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}