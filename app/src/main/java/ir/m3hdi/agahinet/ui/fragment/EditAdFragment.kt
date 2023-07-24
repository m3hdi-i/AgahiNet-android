package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAdFragment : NewAdFragment() {

    private val args: EditAdFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null){
           viewModel.setAdToEdit(args.ad)
        }
    }
}