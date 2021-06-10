package cu.jaco.petstore.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.jaco.petstore.retrofit.utils.suspended
import dagger.hilt.android.lifecycle.HiltViewModel
import io.swagger.petstore.api.StoreApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ApiViewModel @Inject constructor(
    private val api: StoreApi
) : ViewModel() {

    private val _inventory = MutableLiveData<Map<String, Int>>()

    fun getPetsInventory(): LiveData<Map<String, Int>> {

        if (!_inventory.value.isNullOrEmpty())
            return _inventory

        viewModelScope.launch {

            val inventory = withContext(Dispatchers.IO) {
                api.inventory.suspended()
            }

            _inventory.postValue(inventory ?: LinkedHashMap())

        }

        return _inventory

    }

}