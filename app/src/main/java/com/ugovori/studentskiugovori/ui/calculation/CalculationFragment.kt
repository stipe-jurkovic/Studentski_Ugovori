package com.ugovori.studentskiugovori.ui.calculation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.ugovori.studentskiugovori.compose.AppTheme
import com.ugovori.studentskiugovori.databinding.FragmentCalculationBinding

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

        _binding = FragmentCalculationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = binding.composeView

        composeView.setContent {
            AppTheme(){ CalcWholeCompose() }
        }
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                viewLifecycleOwner
            )
        )

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}