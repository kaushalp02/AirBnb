package com.kaushal.projects.airBnbApp.strategy;

import com.kaushal.projects.airBnbApp.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    public BigDecimal calculatePrice(Inventory inventory);

}
