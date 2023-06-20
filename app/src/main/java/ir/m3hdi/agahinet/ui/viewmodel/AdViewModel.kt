package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.domain.model.Resultx
import ir.m3hdi.agahinet.domain.model.onFailure
import ir.m3hdi.agahinet.domain.model.onSuccess
import ir.m3hdi.agahinet.util.AppUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdViewModel @Inject constructor(private val adRepository: AdRepository): ViewModel() {

    lateinit var ad: Ad

    private val _images = MutableSharedFlow<List<String>>(replay = 1)
    val images=_images.asSharedFlow()

    fun getImages(){
        viewModelScope.launch {
            if (ad.mainImageId!=null){
                adRepository.getImagesOfAd(ad.adId).onSuccess {
                    _images.emit(it.map { i-> AppUtils.getImageUrlByImageId(i) })
                }.onFailure {
                    _images.emit(listOf())
                }
            }else{
                _images.emit(listOf())
            }
        }

    }
}