package ir.m3hdi.agahinet.ui.viewmodel

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.model.Ad
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.Resultx
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val adRepository: AdRepository,application: Application) : AndroidViewModel(application)  {

    val adItems= mutableListOf<Ad>()

    private val _nextPage = MutableStateFlow<Resultx<List<Ad>>>(Resultx.success(listOf()))
    val nextPage: StateFlow<Resultx<List<Ad>>>
        get() = _nextPage

    var scrollState:Int?=null

    var isLastPage=false
    var currentPage=0

    private var c=1
    fun fetchNextPage()
    {
        _nextPage.value= Resultx.loading()

        viewModelScope.launch {

            val page= mutableListOf<Ad>()
            for (i in 0..20){
                val ad = Ad(
                    adId = 56,
                    title = "Default Title $c",
                    description = "Default Description",
                    price = "Not Available",
                    createdAt = "2022-01-01",
                    category = 0,
                    creator = 0,
                    city = 0,
                    mainImageId = null
                )
                page.add(ad)
                c++
            }

            delay(2000)

            //val newList = (_ads.value?.getOrNull() ?: emptyList()  ) + page
            adItems+=page

            if (AppUtils.hasInternetConnection(getApplication<Application>().applicationContext)){
                _nextPage.value= Resultx.success(page)
            }else{
                _nextPage.value= Resultx.failure(Exception(""))
            }

            if (c>=110){
                isLastPage=true
            }

            currentPage++


        }


    }

}