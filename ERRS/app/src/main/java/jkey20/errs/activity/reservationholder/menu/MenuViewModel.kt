package jkey20.errs.activity.reservationholder.menu

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jkey20.errs.activity.reservationholder.util.Util
import jkey20.errs.base.BaseViewModel
import jkey20.errs.model.cart.CartMenu
import jkey20.errs.model.firebase.Menu
import jkey20.errs.model.firebase.MenuList
import jkey20.errs.model.firebase.Order
import jkey20.errs.model.firebase.Reservation
import jkey20.errs.repository.FirebaseRepository
import jkey20.errs.repository.toObjectNonNull
import kotlinx.coroutines.flow.*
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

    private val _cartList = MutableStateFlow(listOf<Menu>())
    val cartList = _cartList.asStateFlow()

    private val _reservation = MutableStateFlow(Reservation())
    val reservation = _reservation.asStateFlow()

    private val _deviceToken = MutableStateFlow(String())
    val deviceToken = _deviceToken.asStateFlow()

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

    fun loadReservation(): Reservation {
        return _reservation.value
    }

    fun addCartList(cartList: List<Menu>) = viewModelScope.launch {
        val list = _cartList.value.toMutableList()
        list += cartList
        _cartList.emit(list)
        _cartList.value.forEach { menu ->
            Log.e("CART LIST menu", menu.toString() )
        }
    }

    fun loadCartList() : List<Menu> {
        return _cartList.value
    }
    fun setToken(token: String) = viewModelScope.launch {
        Log.i("Token", token)
        _deviceToken.emit(token)
    }

    fun getDeviceToken(): String {
        return _deviceToken.value
    }

    fun addMenuList(restaurantName: String) = viewModelScope.launch {
        runCatching {
            repository.readMenuInfos(restaurantName)
        }.onSuccess { value ->
            val menuList: MenuList = value.toObjectNonNull()
            val list = mutableListOf<Menu>()
            menuList.menuList.forEach { menu ->
                Log.e("addMenuList menu", menu.toString())
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

    fun addOrder(restaurantName: String, order :Order) = viewModelScope.launch {
        runCatching {
            repository.updateOrderMenu(restaurantName, getDeviceToken(), order)
        }.onSuccess {isSuccess ->
            Log.i("메뉴주문성공", isSuccess.toString())
            checkMyReservation(loadRestaurantName())
        }. onFailure { exception ->
            Log.i("메뉴주문실패", exception.message.toString())
        }
    }

    fun checkMyReservation(restaurantName: String) = viewModelScope.launch {
        runCatching {
            repository.readReservationList(restaurantName)
        }.onSuccess { value ->


            // 전체 리스트 조회

            value.documents.forEach { documentSnapshot ->
                if (documentSnapshot.id == getDeviceToken()) {
                    Log.e("dc.ids pass","!!")
                    _reservation.emit(documentSnapshot.toObjectNonNull())
                }
                Log.e("dc.ids",documentSnapshot.id)
                Log.e("devtoken",getDeviceToken())
                Log.e("checkMyReservation documentSnapshot", documentSnapshot.data.toString())
            }

        }
    }

    private fun hasMenuCorrectUri(uri: Uri, i: Int) =
        uri.toString().contains("menu${i + 1}")

}