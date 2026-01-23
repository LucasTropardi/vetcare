package com.lucast.vetcare.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(
        name = "Health",
        description = "Service health and availability endpoints"
)
public class PingController {

    @GetMapping("/ping")
    @Operation(
            summary = "Ping service",
            description = "Checks if the VetCare API is up and running"
    )
    public Map<String, Object> ping() {
        return Map.of(
                "service", "vetcare",
                "status", "ok",
                "nowUtc", Instant.now().toString()
        );
    }
}
