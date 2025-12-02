package com.grupoTelemedicina.PlataformaTelemedicina.dto;

public class PaymentIntentResponse {
    private String clientSecret;
    public PaymentIntentResponse(String clientSecret) { 
        this.clientSecret = clientSecret; }
    public String getClientSecret() { 
        return clientSecret; }
    public void setClientSecret(String clientSecret) { 
        this.clientSecret = clientSecret; }
}