package gr.ianic.smartville.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

abstract class BaseComposeFragment : Fragment() {

    @Composable
    abstract fun setupComposeViews()
    abstract fun fetch()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fetch()
        return ComposeView(requireContext()).apply {
            setContent {
                setupComposeViews()
            }
        }
    }
}