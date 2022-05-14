package jkey20.errs.activity.reservationholder.menu

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jkey20.errs.base.BaseViewModel
import jkey20.errs.model.firebase.Menu
import jkey20.errs.repository.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(private val repository: ServerRepository) :
    BaseViewModel() {
    private val _menuList = MutableStateFlow(listOf<Menu>())
    val menuList = _menuList.asStateFlow()

    fun loadMenuList() : List<Menu>{
        return _menuList.value
    }

    fun addMenuList() = viewModelScope.launch {
        repository.readMenu()
        var list = arrayListOf<Menu>()

        list.add(Menu("1번 메뉴", 1000, info = "1번 메뉴입니다."))
        list.add(Menu("2번 메뉴", 2000, info = "2번 메뉴입니다."))
        list.add(Menu("3번 메뉴", 1000, info = "3번 메뉴입니다."))
        list.add(Menu("4번 메뉴", 2000, info = "4번 메뉴입니다."))
        list.add(Menu("1번 메뉴", 1000, info = "1번 메뉴입니다."))
        list.add(Menu("2번 메뉴", 2000, info = "2번 메뉴입니다."))
        list.add(Menu("3번 메뉴", 1000, info = "3번 메뉴입니다."))
        list.add(Menu("4번 메뉴", 2000, info = "4번 메뉴입니다."))
        list.add(Menu("1번 메뉴", 1000, info = "1번 메뉴입니다."))
        list.add(Menu("2번 메뉴", 2000, info = "2번 메뉴입니다."))
        list.add(Menu("3번 메뉴", 1000, info = "3번 메뉴입니다."))
        list.add(Menu("4번 메뉴", 2000, info = "4번 메뉴입니다."))
        list.add(Menu("1번 메뉴", 1000, info = "1번 메뉴입니다."))
        list.add(Menu("2번 메뉴", 2000, info = "2번 메뉴입니다."))
        list.add(Menu("3번 메뉴", 1000, info = "3번 메뉴입니다."))
        list.add(Menu("4번 메뉴", 2000, info = "4번 메뉴입니다."))
        _menuList.emit(list)
    }
}