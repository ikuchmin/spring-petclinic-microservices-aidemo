package org.springframework.samples.petclinic.customers.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("petbase.api")
public class PetBaseApiProperties {

    private String baseUrl;

    private String bearerToken;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }
}
