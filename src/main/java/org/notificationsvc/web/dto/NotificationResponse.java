package org.notificationsvc.web.dto;

import lombok.Builder;
import lombok.Data;
import org.notificationsvc.model.NotificationStatus;
import org.notificationsvc.model.NotificationType;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private String subject;

    private LocalDateTime createdOn;

    private NotificationStatus status;

    private NotificationType type;
}
