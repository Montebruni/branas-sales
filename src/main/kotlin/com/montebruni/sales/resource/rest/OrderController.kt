package com.montebruni.sales.resource.rest

import com.montebruni.sales.resource.rest.request.CalculateFreightRequest
import com.montebruni.sales.resource.rest.request.CreateOrderRequest
import com.montebruni.sales.resource.rest.request.toCalculateFreightInput
import com.montebruni.sales.resource.rest.request.toCreateOrderInput
import com.montebruni.sales.resource.rest.response.CreateOrderResponse
import com.montebruni.sales.usecase.CalculateFreight
import com.montebruni.sales.usecase.CreateOrder
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/orders")
class OrderController(
    private val createOrder: CreateOrder,
    private val calculateFreight: CalculateFreight
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createOrder(@RequestBody body: CreateOrderRequest): CreateOrderResponse =
        createOrder.execute(body.toCreateOrderInput()).let {
            CreateOrderResponse(
                orderId = it.orderId,
                totalAmount = it.totalAmount
            )
        }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/calculateFreight")
    fun calculateFreight(@RequestBody body: CalculateFreightRequest): Double =
        calculateFreight.execute(body.toCalculateFreightInput())
}
