package com.example.studentskiugovori.ui.fulllist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.studentskiugovori.compose.FullListCompose
import com.example.studentskiugovori.databinding.FragmentFulllistBinding
import com.example.studentskiugovori.ui.home.HomeViewModel
import com.example.studomatisvu.compose.HomeCompose
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

        val homeViewModel: HomeViewModel by KoinJavaComponent.inject(HomeViewModel::class.java)

        _binding = FragmentFulllistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val sharedPref = context?.let {
            EncryptedSharedPreferences.create(
                "PreferencesFilename",
                masterKeyAlias,
                it,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        val composeView = binding.composeView
        //homeViewModel.getData()

        homeViewModel.ugovori.observe(viewLifecycleOwner) {
            composeView.setContent {
                FullListCompose(homeViewModel)
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