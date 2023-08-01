package ir.m3hdi.agahinet.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.data.repository.CityRepository
import ir.m3hdi.agahinet.domain.model.ad.Ad
import ir.m3hdi.agahinet.domain.model.ad.Category
import ir.m3hdi.agahinet.domain.model.ad.FilterTag
import ir.m3hdi.agahinet.domain.model.ad.SearchFilters
import ir.m3hdi.agahinet.util.Constants.Companion.ENTIRE_IRAN_CITY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private const val DEBOUNCE_TIMEOUT_MS=500L
private val initialSearchFilters= SearchFilters()

@HiltViewModel
class HomeViewModel @Inject constructor(private val adRepository: AdRepository,private val cityRepository: CityRepository,application: Application) : AndroidViewModel(application)  {

    private val _filters= MutableStateFlow(initialSearchFilters)
    val filters=_filters.asStateFlow()

    private val searchQueryPublishSubject= PublishSubject.create<String>()
    private val rxCompositeDisposable = CompositeDisposable()

    private var currentProvince:City = ENTIRE_IRAN_CITY

    private val _tempCurrentProvince= MutableStateFlow(ENTIRE_IRAN_CITY)
    val tempCurrentProvince=_tempCurrentProvince.asStateFlow()
    var tempCategory: Category?=null
    var tempCities:List<City>?=null
    var tempMinPrice:String?=null
    var tempMaxPrice:String?=null


    private val search = MutableSharedFlow<SearchFilters>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow = search
        .onStart { emit(filters.value) }
        .flatMapLatest { getNewSearchFlow(it) }
        .cachedIn(viewModelScope)

    init {

        rxCompositeDisposable.addAll(
            // Search box control
            searchQueryPublishSubject
                .distinctUntilChanged()
                .debounce(DEBOUNCE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    _filters.value = filters.value.copy(keyword=it.ifBlank { null })
                }
        )

        viewModelScope.launch {
            // Observe filters and do search when they change
            launch {
                filters.collect{
                    search.emit(it)
                }
            }
        }

    }

    fun setSearchQuery(query: String) = searchQueryPublishSubject.onNext(query)


    private fun getNewSearchFlow(filters: SearchFilters): Flow<PagingData<Ad>> {
        return adRepository.searchAds(filters)
    }

    /*
     * Function to call from filters chips close buttons in home screen
     */
    fun clearFilterTag(filterTag: FilterTag){
        _filters.value = when (filterTag) {
            is FilterTag.CATEGORY -> filters.value.copy(category = null)
            is FilterTag.PRICE -> filters.value.copy(minPrice = null, maxPrice = null)
            is FilterTag.CITY -> {
                val newCities=filters.value.cities?.minus(filterTag.city).takeIf { !it.isNullOrEmpty() }
                filters.value.copy(cities = newCities)
            }
        }
    }

    /***
     *
     * Functions related to filters change screens
     *
     */

    fun fillTempFilters(){
        _filters.value.let {
            tempCategory=it.category
            tempCities=it.cities
            tempMinPrice=it.minPrice
            tempMaxPrice=it.maxPrice
        }
        _tempCurrentProvince.value = currentProvince
    }

    fun applyTempFilters(){
         _filters.value= _filters.value.copy(
             category=tempCategory,
             cities=tempCities,
             minPrice=tempMinPrice,
             maxPrice=tempMaxPrice
         )
        currentProvince=tempCurrentProvince.value
    }

    fun setTempCurrentProvince(province:City){
        _tempCurrentProvince.value=province
    }



    override fun onCleared() {
        super.onCleared()
        rxCompositeDisposable.dispose()
    }
}