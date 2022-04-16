package com.example.inventory

import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch

class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    fun isItemValid(name: String, price: String, count: String): Boolean =
        name.isNotBlank() && price.isNotBlank() && count.isNotBlank()

    fun addNewItem(name: String, price: String, count: String) =
        insertItem(getNewItem(name, price, count))

    fun retrieveItem(id: Int): LiveData<Item> = itemDao.getItem(id).asLiveData()

    fun isStockAvailable(item: Item): Boolean = item.quantityInStock > 0

    fun sellItem(item: Item) {
        if (item.quantityInStock > 0) {
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    fun updateItem(id: Int, name: String, price: String, count: String) {
        val updatedItem = getUpdatedItem(id, name, price, count)
        updateItem(updatedItem)
    }

    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    private fun getNewItem(name: String, price: String, count: String): Item =
        Item(
            name = name,
            price = price.toDouble(),
            quantityInStock = count.toInt()
        )

    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    private fun getUpdatedItem(id: Int, name: String, price: String, count: String): Item =
        Item(id = id, name = name, price = price.toDouble(), quantityInStock = count.toInt())
}

class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}