package org.springframework.samples.petclinic.customers.client;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PetBaseServiceClient {

    private final RestClient.Builder restClientBuilder;

    private final PetBaseApiProperties petBaseApiProperties;

    public PetBaseServiceClient(RestClient.Builder restClientBuilder,
                                PetBaseApiProperties petBaseApiProperties) {
        this.restClientBuilder = restClientBuilder;
        this.petBaseApiProperties = petBaseApiProperties;
    }

    public PetBaseInfo getPetInfoByChipId(String chipId) {
        RestClient petClient = restClientBuilder.baseUrl(petBaseApiProperties.getBaseUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + petBaseApiProperties.getBearerToken())
            .build();
        return petClient.get().uri("/by-chip-id").retrieve().body(PetBaseInfo.class);
    }
}
