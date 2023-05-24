package ir.m3hdi.agahinet.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.data.model.Ad
import ir.m3hdi.agahinet.data.model.AdFilters
import ir.m3hdi.agahinet.data.model.Category
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.util.Constants.Companion.PAGE_SIZE
import ir.m3hdi.agahinet.data.Resultx
import ir.m3hdi.agahinet.data.local.dao.CityDao
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.data.onFailure
import ir.m3hdi.agahinet.data.onSuccess
import ir.m3hdi.agahinet.util.Constants.Companion.ENTIRE_IRAN_CITY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val adRepository: AdRepository,application: Application,private val cityDao: CityDao) : AndroidViewModel(application)  {

    val adItems= mutableListOf<Ad>()

    private val _filters = MutableLiveData(AdFilters())
    val filters :LiveData<AdFilters> get() = _filters

    private val _nextPage = MutableStateFlow<Resultx<List<Ad>>>(Resultx.success(listOf()))
    val nextPage= _nextPage.asStateFlow()

    private val _homeRvClear= MutableSharedFlow<Unit>()
    val homeRvClear= _homeRvClear.asSharedFlow()

    private val fetchNextPagePublishSubject= PublishSubject.create<Unit>()
    private val searchQueryPublishSubject= PublishSubject.create<String>()
    private val rxCompositeDisposable = CompositeDisposable()

    private val _currentProvince= MutableStateFlow(ENTIRE_IRAN_CITY)
    val currentProvince=_currentProvince.asStateFlow()

    var tempCategory:Category?=null
    var tempCities:List<City>?=null
    var tempMinPrice:String?=null
    var tempMaxPrice:String?=null

    var isLastPage = false


    init {

        rxCompositeDisposable.addAll(
            // Pagination control
            fetchNextPagePublishSubject
                .filter{ !isLastPage && !nextPage.value.isLoading }
                .throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    _filters.value= _filters.value?.apply { offset=adItems.size }
                    fetchAdsByFilters()
            },
            // Search box control
            searchQueryPublishSubject
                .distinctUntilChanged()
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    viewModelScope.launch {
                        _homeRvClear.emit(Unit)
                    }
                    _filters.value=_filters.value?.apply { keyword=it; offset=0 }
                    isLastPage=false
                    fetchAdsByFilters()
            }
        )

        // Start App with showing all recent ads
        searchQueryPublishSubject.onNext("")

    }


    private var networkJob:Job?=null
    private fun fetchAdsByFilters()
    {
        networkJob?.cancel()

        networkJob=viewModelScope.launch {

            _nextPage.value= Resultx.loading()

            adRepository.searchAds(_filters.value!!).onSuccess {
                if (isActive){
                    if (it.size < PAGE_SIZE){
                        isLastPage=true
                    }
                    adItems+=it
                    _nextPage.value= Resultx.success(it)
                }

            }.onFailure {
                if (isActive){
                    _nextPage.value= Resultx.failure(it)
                }

            }
        }

    }

    fun search(query:String) = searchQueryPublishSubject.onNext(query)

    fun fetchNextPage() = fetchNextPagePublishSubject.onNext(Unit)


    private var _allProvincesList:List<City>?=null
    suspend fun getAllProvinces():List<City>{
        return _allProvincesList ?: withContext(Dispatchers.IO){
            val plist = mutableListOf(ENTIRE_IRAN_CITY) + cityDao.getAllProvinces()
            _allProvincesList = plist
            return@withContext plist
        }
    }
    suspend fun getCitiesOfProvince(provinceId:Int) = withContext(Dispatchers.IO){
        return@withContext cityDao.getCitiesOfProvince(provinceId)
    }


    fun fillTempFilters(){
        _filters.value?.let {
            tempCategory=it.category
            tempCities=it.cities
            tempMinPrice=it.minPrice
            tempMaxPrice=it.maxPrice
        }
    }

    fun applyTempFilters(){
         _filters.value= _filters.value?.apply {
             category=tempCategory
             cities=tempCities
             minPrice=tempMinPrice
             maxPrice=tempMaxPrice
         }
    }

    fun setCurrentProvince(city:City){
        // Provinces must have a null parent
        if (city.parentProvinceId==null){
            _currentProvince.value=city
        }
    }

    override fun onCleared() {
        super.onCleared()
        rxCompositeDisposable.dispose()
    }
}