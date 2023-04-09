package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {

    val isAuthed = MutableLiveData<Boolean>().apply {
        value = false
    }
}