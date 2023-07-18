package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.domain.model.ContactInfo
import ir.m3hdi.agahinet.domain.model.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactInfoViewModel  @Inject constructor(private val adRepository: AdRepository) : ViewModel() {

    private val _contactInfo = MutableStateFlow<ContactInfo?>(null)
    val contactInfo = _contactInfo.asStateFlow()

    fun getContactInfo(userId:Int){
        viewModelScope.launch {
            adRepository.getContactInfoOfUser(userId).onSuccess {
                _contactInfo.value = it
            }
        }
    }

}