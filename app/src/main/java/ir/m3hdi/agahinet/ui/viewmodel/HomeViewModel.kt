package ir.m3hdi.agahinet.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.m3hdi.agahinet.data.model.Ad
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.Resultx
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val adRepository: AdRepository,application: Application) : AndroidViewModel(application)  {

    private val _ads = MutableLiveData<Resultx<List<Ad>>>()
    val ads: LiveData<Resultx<List<Ad>>>
        get() = _ads

    var isLastPage=false
    var currentPage=0

    var c=1
    fun fetchNextPage()
    {
        _ads.value= Resultx.loading()

        viewModelScope.launch {

            val page= mutableListOf<Ad>()
            for (i in 0..4){
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

            val newList = (_ads.value?.getOrNull() ?: emptyList()  ) + page


            if (AppUtils.hasInternetConnection(getApplication<Application>().applicationContext)){
                _ads.value= Resultx.success(newList)
            }else{
                _ads.value= Resultx.failure(Exception(""))
            }


        }


    }

}