package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.domain.model.onFailure
import ir.m3hdi.agahinet.domain.model.onSuccess
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.Constants.Companion.CATEGORIES
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

    var selectedImagePosition = 0

    private val _bookmarkState = MutableStateFlow(false)
    val bookmarkState = _bookmarkState.asStateFlow()

    private val _uiEventFlow = MutableSharedFlow<UiEvent>()
    val uiEventFlow=_uiEventFlow.asSharedFlow()

    fun initAd(ad: Ad){
        this.ad = ad
        getImages()
        checkIfBookmarked()
    }

    private fun checkIfBookmarked(){
        if (AppUtils.currentUser.value != null){
            viewModelScope.launch {
                adRepository.hasBookmark(ad.adId).onSuccess {
                    _bookmarkState.value = it.has
                }
            }
        }
    }

    private fun getImages(){
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

    fun getCategoryTitle() = CATEGORIES.find { it.id==ad.category }?.title ?: CATEGORIES[0].title

    fun setBookmarked(bookmark: Boolean) {
        viewModelScope.launch {
            if (AppUtils.currentUser.value!=null){
                if (bookmark){
                    adRepository.addBookmark(ad.adId).onSuccess {
                        _bookmarkState.value = true
                        _uiEventFlow.emit(UiEvent.BookmarkSetOK(true))

                    }.onFailure {
                        _uiEventFlow.emit(UiEvent.FailedToSetBookmark())
                    }
                }else{
                    adRepository.deleteBookmark(ad.adId).onSuccess {
                        _bookmarkState.value = false
                        _uiEventFlow.emit(UiEvent.BookmarkSetOK(false))
                    }.onFailure {
                        _uiEventFlow.emit(UiEvent.FailedToSetBookmark())
                    }
                }
            }else{
                _uiEventFlow.emit(UiEvent.AuthenticationRequired)
                _uiEventFlow.emit(UiEvent.FailedToSetBookmark(showToast = false))
            }

        }
    }

    sealed class UiEvent {
        data class BookmarkSetOK(val bookmark: Boolean) : UiEvent()
        data class FailedToSetBookmark(val showToast:Boolean=true) : UiEvent()
        object AuthenticationRequired : UiEvent()
    }

}

