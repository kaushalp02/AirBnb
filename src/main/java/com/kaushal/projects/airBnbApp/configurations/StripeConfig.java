package com.kaushal.projects.airBnbApp.configurations;


import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    public StripeConfig(@Value("${stripe.secretKey}")String stripeSecret)
    {
        Stripe.apiKey = stripeSecret;
    }
    
}

