package ec.edu.ups.academic_events_api.core.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "service", "Spring Boot API",
                "status", "running",
                "timestamp", LocalDateTime.now().toString());
    }
}
