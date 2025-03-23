package org.notificationsvc.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ContactMessageResponse {
   private UUID id;
   private UUID userId;
   private String email;
   private String subject;
   private String message;
}
