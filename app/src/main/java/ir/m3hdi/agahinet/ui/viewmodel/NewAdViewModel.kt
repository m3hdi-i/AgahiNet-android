package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewAdViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is new ad Fragment"
    }
    val text: LiveData<String> = _text
}