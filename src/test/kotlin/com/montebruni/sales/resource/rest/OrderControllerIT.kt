package com.montebruni.sales.resource.rest

import com.montebruni.sales.application.usecase.CreateOrder
import com.montebruni.sales.application.usecase.FindOrderByOrderNumber
import com.montebruni.sales.application.usecase.GetAllOrders
import com.montebruni.sales.application.usecase.input.CreateOrderInput
import com.montebruni.sales.common.BaseRestIT
import com.montebruni.sales.fixture.resource.rest.createOrderRequest
import com.montebruni.sales.fixture.usecase.createCreateOrderOutput
import com.montebruni.sales.fixture.usecase.createOrderOutput
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [OrderController::class])
class OrderControllerIT : BaseRestIT() {

    @MockkBean
    private lateinit var createOrder: CreateOrder

    @MockkBean
    private lateinit var findOrderByOrderNumber: FindOrderByOrderNumber

    @MockkBean
    private lateinit var getAllOrders: GetAllOrders

    private val baseUrl = "/v1/orders"

    @AfterEach
    internal fun tearDown() {
        confirmVerified(createOrder, findOrderByOrderNumber, getAllOrders)
    }

    @Nested
    @DisplayName("checkout test")
    inner class CheckoutTestCases {

        @Test
        fun `should return output when creation is successfully`() {
            val request = createOrderRequest()
            val expectedOutput = createCreateOrderOutput()

            val useCaseSlot = slot<CreateOrderInput>()

            every { createOrder.execute(capture(useCaseSlot)) } returns expectedOutput

            mockMvc.perform(
                post("$baseUrl/checkout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
            )
                .andExpect(status().is2xxSuccessful)
                .andExpect(jsonPath("order_number").value(expectedOutput.orderNumber))
                .andExpect(jsonPath("total_amount").value(expectedOutput.totalAmount.toString()))
                .run {
                    assertEquals(request.document, useCaseSlot.captured.document)
                    assertEquals(request.items.size, useCaseSlot.captured.items.size)
                    assertNull(useCaseSlot.captured.coupon)
                }

            verify { createOrder.execute(useCaseSlot.captured) }
        }
    }

    @Nested
    @DisplayName("Get by order number")
    inner class GetByOrderNumberTestCases {

        @Test
        fun `should retrieve order when given a valid order number`() {
            val orderNumber = "202300000001"
            val expectedOutput = createOrderOutput()

            every { findOrderByOrderNumber.execute(orderNumber) } returns expectedOutput

            mockMvc.perform(
                get("$baseUrl/$orderNumber").contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().is2xxSuccessful)
                .andExpect(jsonPath("id").value(expectedOutput.id.toString()))
                .andExpect(jsonPath("order_number").value(expectedOutput.orderNumber))

            verify { findOrderByOrderNumber.execute(orderNumber) }
        }
    }

    @Nested
    @DisplayName("Get orders")
    inner class GetOrdersTestCases {

        @Test
        fun `should retrieve all saved orders`() {
            val expectedOutput = listOf(
                createOrderOutput(),
                createOrderOutput()
            )

            every { getAllOrders.execute() } returns expectedOutput

            mockMvc.perform(
                get(baseUrl).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().is2xxSuccessful)
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedOutput.size))

            verify { getAllOrders.execute() }
        }
    }
}
