package com.grupoTelemedicina.PlataformaTelemedicina.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    private String secretKey = "sk_test_51SZwQJRXZrkYvZdtJkBQHQOF52vz5sbiHwfnGaYb2pCHz4GLlpm0BjxR1k4yHmbSnnP5A9TWnJe1aoIseYZBIO7x00dB73S2LS";
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
}