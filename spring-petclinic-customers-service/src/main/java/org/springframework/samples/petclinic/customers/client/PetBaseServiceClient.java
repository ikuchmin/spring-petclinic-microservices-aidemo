package org.springframework.samples.petclinic.customers.client;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PetBaseServiceClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${petBase.api.base-url}")
    private String petbaseApiBaseUrl;

    @Value("${petBase.api.bearer-token}")
    private String petbaseApiBearerToken;

    public PetBaseServiceClient(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public PetBaseInfo getPetInfoByChipId(String chipId) {
        RestClient petClient = restClientBuilder.baseUrl(petbaseApiBaseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + petbaseApiBearerToken)
            .build();
        return petClient.get().uri("/by-chip-id").retrieve().body(PetBaseInfo.class);
    }
}
