package jkey20.errs.activity.reservationholder.menu

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jkey20.errs.base.BaseViewModel
import jkey20.errs.model.firebase.Menu
import jkey20.errs.model.firebase.Order
import jkey20.errs.repository.FirebaseRepository
import jkey20.errs.repository.toObjectNonNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(private val repository: FirebaseRepository) :
    BaseViewModel() {

    private val _restaurantName = MutableStateFlow("")
    val restaurantName = _restaurantName.asStateFlow()

    private val _menuList = MutableStateFlow(listOf<Menu>())
    val menuList = _menuList.asStateFlow()

    private val _uriList = MutableStateFlow(listOf<Uri>())
    val uriList = _uriList.asStateFlow()

    fun loadRestaurantName(): String {
        return _restaurantName.value
    }

    fun setRestaurantName(restaurantName: String) {
        _restaurantName.value = restaurantName
    }

    fun loadMenuList(): List<Menu> {
        return _menuList.value
    }

    fun loadUriList(): List<Uri> {
        return _uriList.value
    }

    fun addMenuList(restaurantName: String) = viewModelScope.launch {
        runCatching {
            repository.readMenuInfos(restaurantName)
        }.onSuccess { value ->
            val order: Order = value.toObjectNonNull()
            val list = mutableListOf<Menu>()
            order.menuList.forEach { menu ->
                list.add(menu)
            }
            _menuList.emit(list)
        }.onFailure { error ->
            Log.e("addMenuList error", error.message.toString())
        }

    }

    fun addMenuImageList(restaurantName: String) = viewModelScope.launch {
        runCatching {
            repository.readMenuImages(restaurantName)
        }.onSuccess { uriList ->
            uriList.forEach { uri -> Log.e("URI CHECK", uri.toString()) }
            _uriList.emit(uriList)
        }.onFailure { error ->
            // error
            Log.e("onFAILURE", error.message.toString())
        }

    }

    fun getMenuList(uriList: List<Uri>, menuList: List<Menu>): List<Menu> {
        val list = mutableListOf<Menu>()
        for (i in uriList.indices) {
            uriList.forEach { uri ->
                if (hasMenuCorrectUri(uri, i)) {
                    list.add(menuList[i].copy(uri = uri.toString()))
                }
            }
        }
        return list
    }

    private fun hasMenuCorrectUri(uri: Uri, i: Int) =
        uri.toString().contains("menu${i + 1}")

}