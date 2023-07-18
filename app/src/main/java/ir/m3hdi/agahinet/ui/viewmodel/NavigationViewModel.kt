package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NavigationViewModel :ViewModel(){

    private val _goToAuthScreenEvent= MutableSharedFlow<Unit>()
    val goToAuthScreenEvent = _goToAuthScreenEvent.asSharedFlow()

    fun goToAuthenicateScreen(){
        viewModelScope.launch {
            _goToAuthScreenEvent.emit(Unit)
        }
    }

}