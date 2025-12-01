package com.voicecalc.model

import java.util.UUID

/**
 * Represents a single item in the calculation list (number or operation)
 */
data class InputItem(
    val id: String = UUID.randomUUID().toString(),
    var value: String,
    val type: ItemType,
    var isEdited: Boolean = false
) {
    enum class ItemType {
        NUMBER,
        OPERATION
    }

    fun isNumber(): Boolean = type == ItemType.NUMBER
    fun isOperation(): Boolean = type == ItemType.OPERATION

    override fun toString(): String = value
}
