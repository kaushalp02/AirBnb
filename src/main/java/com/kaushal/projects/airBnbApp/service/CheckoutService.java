package com.kaushal.projects.airBnbApp.service;

import com.kaushal.projects.airBnbApp.entity.Booking;

public interface CheckoutService {

    public String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
