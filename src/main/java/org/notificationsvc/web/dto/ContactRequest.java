package org.notificationsvc.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class ContactRequest {

    private UUID id;
    private String username;
    private UUID userId;
    private String email;
    private String subject;
    private String message;
}

