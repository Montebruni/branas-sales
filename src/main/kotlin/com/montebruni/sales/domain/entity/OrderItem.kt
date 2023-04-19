package com.montebruni.sales.domain.entity

import com.montebruni.sales.domain.valueobjects.Amount
import java.util.*

data class OrderItem(
    val id: UUID = UUID.randomUUID(),
    val orderId: UUID,
    val product: Product,
    val price: Amount,
    val quantity: Int
) {

    init {
        if (quantity < 1) throw IllegalArgumentException("Invalid quantity")
    }

    fun calculateTotalAmount(): Amount = price.multiply(quantity)
}
