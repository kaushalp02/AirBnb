package com.kaushal.projects.airBnbApp.strategy;

import com.kaushal.projects.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        if (!inventory.getDate().isBefore(LocalDate.now()) && inventory.getDate().isBefore(LocalDate.now().plusDays(7)))
        {
            price = price.multiply(BigDecimal.valueOf(1.15));
        }

        return price;
    }
}
