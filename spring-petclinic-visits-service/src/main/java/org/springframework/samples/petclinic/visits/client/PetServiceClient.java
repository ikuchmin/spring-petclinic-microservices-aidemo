/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.visits.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Client for interacting with the customers-service to retrieve pet information.
 *
 * @author Igor Kuchmin
 */
@Component
public class PetServiceClient {


    private final RestTemplate loadBalancedRestTemplate;

    public PetServiceClient(RestTemplate loadBalancedRestTemplate) {
        this.loadBalancedRestTemplate = loadBalancedRestTemplate;
    }

    /**
     * Retrieves pet details by pet ID from the customers-service.
     *
     * @param petId the ID of the pet to retrieve
     * @return a Mono containing the pet details
     */
    public PetDetails getPetById(final int ownerId, final int petId) {
        return loadBalancedRestTemplate.getForObject(
            "http://customers-service/owners/{ownerId}/pets/{petId}", PetDetails.class,
            Map.of("ownerId", ownerId, "petId", petId));
    }
}
