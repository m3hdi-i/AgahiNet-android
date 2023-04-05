package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


//@HiltViewModel
class AuthViewModel : ViewModel() {

    val currentState = MutableLiveData<String>()

    fun setState(){
        currentState.value=""
    }
}