package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.m3hdi.agahinet.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NewAdViewModel : ViewModel() {


    private val _newAd = MutableStateFlow(NewAd())
    val newAd = _newAd.asStateFlow()

    fun setCategory(catTitle:String){
        _newAd.value = _newAd.value.copy(category = (Constants.CATEGORIES.find { it.title == catTitle }?.id) ?:0)
    }

    fun setTitle(title: String) {
        _newAd.value = _newAd.value.copy(title = title)
    }

    fun setDescription(desc: String) {
        _newAd.value = _newAd.value.copy(description = desc)
    }

    fun setPrice(price: String?) {
        _newAd.value = _newAd.value.copy(price = price)
    }

}

data class NewAd(
    val title: String="",
    val description: String="",
    val price: String?="",
    val category: Int?=null,
    val city: Int?=null,
    val imagesList: List<String>?=null,
    val mainImageId: String?=null
)