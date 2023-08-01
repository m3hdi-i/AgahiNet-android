package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.domain.model.ad.Ad
import ir.m3hdi.agahinet.domain.model.Resultx
import ir.m3hdi.agahinet.domain.model.onSuccess
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAdsViewModel @Inject constructor(private val adRepository: AdRepository): ViewModel() {

    private val _myAds = MutableLiveData<Resultx<List<Ad>>>()
    val myAds: LiveData<Resultx<List<Ad>>> get() = _myAds

    init {
        getMyAds()
    }

    fun getMyAds(){
        viewModelScope.launch {
            _myAds.value= Resultx.loading()
            _myAds.value = adRepository.getMyAds()
        }
    }
    fun deleteAd(ad: Ad){
        viewModelScope.launch {
            adRepository.deleteAd(ad.adId).onSuccess {
                getMyAds()
            }
        }
    }

}