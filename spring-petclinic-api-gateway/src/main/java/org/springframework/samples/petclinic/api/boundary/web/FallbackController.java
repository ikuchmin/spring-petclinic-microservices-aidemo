package org.springframework.samples.petclinic.api.boundary.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @PostMapping("/fallback")
    public ResponseEntity<String> fallback() {
        return ResponseEntity.status(503)
                .body("Chat is currently unavailable. Please try again later.");
    }
}
