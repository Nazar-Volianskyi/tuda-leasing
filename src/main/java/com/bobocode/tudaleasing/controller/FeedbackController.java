package com.bobocode.tudaleasing.controller;

import com.bobocode.tudaleasing.dto.FeedbackRequest;
import com.bobocode.tudaleasing.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FeedbackController {

    private final EmailService emailService;

    public FeedbackController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> receiveFeedback(@RequestBody FeedbackRequest request) {
        try {
            emailService.sendFeedbackEmail(
                    request.name(),
                    request.email(),
                    request.topic(),
                    request.message()
            );
            return ResponseEntity.ok("Form sent");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error");
        }
    }
}
