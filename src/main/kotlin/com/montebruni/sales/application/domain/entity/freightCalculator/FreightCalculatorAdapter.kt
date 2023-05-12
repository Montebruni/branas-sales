package com.montebruni.sales.application.domain.entity.freightCalculator

import com.montebruni.sales.application.domain.entity.Freight
import com.montebruni.sales.application.domain.entity.freightCalculator.handlers.VolumeFreightCalculator
import com.montebruni.sales.application.domain.port.FreightCalculator
import com.montebruni.sales.extensions.toDecimal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FreightCalculatorAdapter : FreightCalculator {

    @Autowired private lateinit var handler: VolumeFreightCalculator

    override fun calculate(input: Freight): Double = input.quantity * handler.calculate(
        FreightCalculatorInput(
            height = input.height.value,
            width = input.width.value,
            length = input.length.value,
            weight = input.weight.value,
            from = FreightCalculatorInput.Coordinates(latitude = input.from.latitude, longitude = input.from.longitude),
            to = FreightCalculatorInput.Coordinates(latitude = input.to.latitude, longitude = input.to.longitude)
        )
    ).toDecimal()
}
