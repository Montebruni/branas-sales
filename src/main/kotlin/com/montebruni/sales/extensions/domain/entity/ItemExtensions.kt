package com.montebruni.sales.extensions.domain.entity

import com.montebruni.sales.application.domain.entity.Item
import com.montebruni.sales.infra.repository.postgresql.model.ItemPostgresqlModel
import java.util.UUID

fun Item.toItemPostgresqlModel(orderId: UUID) = ItemPostgresqlModel(
    id = id,
    productId = product.id,
    quantity = quantity,
    orderId = orderId,
)
