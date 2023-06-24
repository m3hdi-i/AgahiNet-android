package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.domain.model.Resultx
import ir.m3hdi.agahinet.domain.model.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(private val adRepository: AdRepository): ViewModel() {

    private val _bookmarks = MutableLiveData<Resultx<List<Ad>>>()
    val bookmarks: LiveData<Resultx<List<Ad>>> get() = _bookmarks

    init {
        getBookmarks()
    }

    fun getBookmarks(){
        viewModelScope.launch {
            _bookmarks.value= Resultx.loading()
            _bookmarks.value = adRepository.getBookmarks()
        }
    }

    fun deleteBookmark(ad:Ad){
        viewModelScope.launch {
            adRepository.deleteBookmark(ad.adId).onSuccess {
                getBookmarks()
            }
        }
    }

}