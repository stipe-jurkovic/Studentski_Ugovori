package com.example.studentskiugovori.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.studentskiugovori.compose.AppTheme
import com.example.studentskiugovori.compose.CalcCompose
import com.example.studentskiugovori.compose.CalcWholeCompose
import com.example.studentskiugovori.databinding.FragmentCalculationBinding
import com.example.studentskiugovori.ui.home.HomeViewModel
import org.koin.java.KoinJavaComponent

class CalculationFragment : Fragment() {

    private var _binding: FragmentCalculationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel: HomeViewModel by KoinJavaComponent.inject(HomeViewModel::class.java)

        _binding = FragmentCalculationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = binding.composeView
        homeViewModel.getData()

        homeViewModel.ugovori.observe(viewLifecycleOwner) {
            composeView.setContent {
                AppTheme(){ CalcWholeCompose() }
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