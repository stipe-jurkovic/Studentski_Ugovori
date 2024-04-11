package com.ugovori.studentskiugovori.ui.fulllist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.ugovori.studentskiugovori.MainViewModel
import com.ugovori.studentskiugovori.compose.AppTheme
import com.ugovori.studentskiugovori.databinding.FragmentFulllistBinding
import org.koin.java.KoinJavaComponent

class FullListFragment : Fragment() {

    private var _binding: FragmentFulllistBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by KoinJavaComponent.inject(MainViewModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFulllistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = binding.composeView

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