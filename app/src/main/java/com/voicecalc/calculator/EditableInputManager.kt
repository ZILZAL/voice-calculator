package com.voicecalc.calculator

import com.voicecalc.model.InputItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the list of editable input items
 */
class EditableInputManager {

    private val _inputList = MutableStateFlow<List<InputItem>>(emptyList())
    val inputList: StateFlow<List<InputItem>> = _inputList.asStateFlow()

    fun addItem(item: InputItem) {
        val currentList = _inputList.value.toMutableList()
        currentList.add(item)
        _inputList.value = currentList
    }

    fun updateItem(id: String, newValue: String) {
        val currentList = _inputList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(
                value = newValue,
                isEdited = true
            )
            _inputList.value = currentList
        }
    }

    fun deleteItem(id: String) {
        val currentList = _inputList.value.toMutableList()
        currentList.removeIf { it.id == id }
        _inputList.value = currentList
    }

    fun clear() {
        _inputList.value = emptyList()
    }

    fun getCalculationExpression(): String {
        return _inputList.value.joinToString(" ") { it.value }
    }

    fun getItems(): List<InputItem> {
        return _inputList.value
    }

    fun isEmpty(): Boolean {
        return _inputList.value.isEmpty()
    }
}
