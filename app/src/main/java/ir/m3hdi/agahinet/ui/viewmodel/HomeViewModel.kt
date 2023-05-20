package ir.m3hdi.agahinet.ui.viewmodel

import android.app.Application
import android.util.Log
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
import ir.m3hdi.agahinet.util.Resultx
import ir.m3hdi.agahinet.util.onFailure
import ir.m3hdi.agahinet.util.onSuccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val adRepository: AdRepository,application: Application) : AndroidViewModel(application)  {

    val adItems= mutableListOf<Ad>()

    private val _filters = MutableLiveData(AdFilters())
    val filters :LiveData<AdFilters> get() = _filters

    private val _nextPage = MutableStateFlow<Resultx<List<Ad>>>(Resultx.success(listOf()))
    val nextPage= _nextPage.asStateFlow()

    private val _rvClear= MutableSharedFlow<Unit>()
    val rvClear= _rvClear.asSharedFlow()

    private val fetchNextPagePublishSubject= PublishSubject.create<Unit>()
    private val searchQueryPublishSubject= PublishSubject.create<String>()
    private val rxCompositeDisposable = CompositeDisposable()

    var isLastPage = false

    private var c=1

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
                        _rvClear.emit(Unit)
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


    override fun onCleared() {
        super.onCleared()
        rxCompositeDisposable.dispose()
    }

    fun setCategory(category: Category?) {
        _filters.value=_filters.value?.apply {
            category?.id.let {
                if (it==0)
                    this.category = null
                else
                    this.category = it
            }

        }
    }

    fun getSelectedProvince(): String {

        /*
        filters.value?.cities?.let {
            if (it.isNotEmpty()){
                return " "
            }
         */

        return getApplication<Application>().getString(R.string.entire_iran)
    }


}