package org.notificationsvc.web.dto;

import lombok.Builder;
import lombok.Data;
import org.notificationsvc.model.NotificationType;

import java.util.UUID;

@Data
@Builder
public class NotificationPreferenceResponse {

    private UUID id;

    private UUID userId;

    private NotificationType type;

    private boolean enabled;

    private String contactInfo;
}
