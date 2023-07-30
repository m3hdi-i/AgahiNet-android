package ir.m3hdi.agahinet.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.data.repository.CityRepository
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.domain.model.Category
import ir.m3hdi.agahinet.domain.model.onFailure
import ir.m3hdi.agahinet.domain.model.onSuccess
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.Constants
import ir.m3hdi.agahinet.util.Mappers.Companion.toEditAdRequest
import ir.m3hdi.agahinet.util.Mappers.Companion.toNewAdRequest
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewAdViewModel @Inject constructor(private val cityRepository: CityRepository,private val adRepository: AdRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val eventChannel= Channel<UiEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            val allProvinces = cityRepository.getAllProvinces()
            _uiState.update { it.copy(allProvinces=allProvinces.toImmutableList()) }
        }
    }
    fun setCategory(category:Category){
        _uiState.update { it.copy(category = category) }
    }

    fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun setDescription(desc: String) {
        _uiState.update { it.copy(description = desc) }
    }

    fun setPrice(price: String?) {
        _uiState.update { it.copy(price = price) }
    }

    fun setProvince(province: City) {

        if (uiState.value.province != province){
            viewModelScope.launch {
                val subCities = cityRepository.getCitiesOfProvince(province.cityId)
                _uiState.update {
                    it.copy(province = province, city = null, citiesToSelect = subCities.toImmutableList()) }
            }
        }
    }

    fun setCity(city: City) {
        _uiState.update { it.copy(city = city)  }
    }

    fun addImage(uri: Uri){
        val oldImageList = uiState.value.imagesList
        if (uri !in oldImageList.map { it.uri }){
            val newImageList= (oldImageList + NewAdImage(uri, isMainImage = (oldImageList.size == 0)))
            _uiState.update { it.copy(imagesList = newImageList.toImmutableList())  }
            uploadImage(uri)
        }
    }

    private fun uploadImage(uri: Uri){
        viewModelScope.launch {

            val onFailure = suspend {
                eventChannel.send(UiEvent.FAILED_TO_UPLOAD_IMAGE)
                deleteImage(uri)
            }
            adRepository.uploadImage(uri).onSuccess { result->
                if (result!=null){
                    val newImageList = uiState.value.imagesList.map {
                        if (it.uri == uri) { it.copy(uploadId = result.imageId) } else { it }
                    }
                    println(newImageList.toString())
                    _uiState.update { it.copy(imagesList = newImageList.toImmutableList())  }
                }else{
                    onFailure()
                }
            }.onFailure{
                onFailure()
            }
        }
    }

    fun deleteImage(image: NewAdImage) {
        val oldImageList = uiState.value.imagesList
        val newImageList= (oldImageList - image).toMutableList()

        if (newImageList.firstOrNull { it.isMainImage } == null && newImageList.isNotEmpty()) {
            newImageList[0] = newImageList[0].copy(isMainImage = true)
        }

        _uiState.update { it.copy(imagesList = newImageList.toImmutableList())  }
    }
    private fun deleteImage(uri: Uri) {
        val newImageList= uiState.value.imagesList.filterNot { it.uri==uri }
        _uiState.update { it.copy(imagesList = newImageList.toImmutableList())  }
    }

    fun setMainImage(image: NewAdImage) {

        val newImageList = uiState.value.imagesList.map { item ->
            if (item == image)
                item.copy(isMainImage = true)
            else
                item.copy(isMainImage = false)
        }
        _uiState.update { it.copy(imagesList = newImageList.toImmutableList())  }
    }

    fun publishAd() {
        viewModelScope.launch{
            with(uiState.value){

                if (title.isNotBlank() && description.isNotBlank() && (price==null || price.isNotBlank())
                    && category!=null && province!=null && city!=null){

                    if (imagesList.any { it.uploadId == null }){
                        eventChannel.send(UiEvent.WAIT_TO_UPLOAD_IMAGES)
                    }
                    else {
                        _uiState.update { it.copy(formState = FormState.POSTING) }
                        if(!isEditMode){
                            val newAdRequest = this@with.toNewAdRequest()
                            adRepository.publishAd(newAdRequest).onSuccess {
                                _uiState.update { it.copy(formState = FormState.DONE) }
                            }.onFailure {
                                _uiState.update { it.copy(formState = FormState.IDLE) }
                                eventChannel.send(UiEvent.FAILED_TO_POST)
                            }
                        }else{
                            val editAdRequest = this@with.toEditAdRequest()
                            adRepository.editAd(editAdRequest).onSuccess {
                                _uiState.update { it.copy(formState = FormState.DONE) }
                            }.onFailure {
                                _uiState.update { it.copy(formState = FormState.IDLE) }
                                eventChannel.send(UiEvent.FAILED_TO_POST)
                            }
                        }

                    }

                }else{
                    eventChannel.send(UiEvent.INCOMPLETE_INPUTS)
                }
            }
        }
    }

    fun createNewAd() {
        _uiState.update { UiState() }
    }

    fun setAdToEdit(ad: Ad) {
        viewModelScope.launch {
            _uiState.update {state->
                state.copy(
                    adId=ad.adId,
                    title = ad.title,
                    description = ad.description,
                    price = ad.price,
                    category = Constants.CATEGORIES_FOR_NEW_AD.find { it.id==ad.category },
                    isEditMode = true
                )
            }
            _uiState.update {
                it.copy(province= cityRepository.getParentProvinceOfCity(ad.city),
                    city= cityRepository.getCityById(ad.city),)
            }
            adRepository.getImagesOfAd(ad.adId).onSuccess {images->
                val imagesUri = images.map {
                    val url = AppUtils.getImageUrlByImageId(it)
                    NewAdImage(uri=Uri.parse(url), isMainImage = it==ad.mainImageId, uploadId = it)
                }
                _uiState.update {
                    it.copy(imagesList = imagesUri.toImmutableList(), oldImages = images)
                }
            }

        }

    }


    @Immutable
     data class UiState(
        val title: String="",
        val description: String="",
        val price: String?="",
        val category: Category?=null,
        val province: City?=null,
        val city: City?=null,
        val imagesList: ImmutableList<NewAdImage> = persistentListOf(),

        val allProvinces:ImmutableList<City> = persistentListOf(),
        val citiesToSelect:ImmutableList<City> = persistentListOf(),

        val isEditMode: Boolean = false,
        val formState: FormState = FormState.IDLE,

        val oldImages:List<Long>? = null,
        val adId:Long? = null
    )


}

const val MAX_IMAGES_COUNT = 6

@Immutable
data class NewAdImage(val uri:Uri, val isMainImage:Boolean=false, val uploadId:Long?=null)

enum class FormState {
    IDLE,
    POSTING,
    DONE,
}

enum class UiEvent {
    INCOMPLETE_INPUTS,
    WAIT_TO_UPLOAD_IMAGES,
    FAILED_TO_UPLOAD_IMAGE,
    FAILED_TO_POST
}