package org.notificationsvc.web.mapper;



import org.notificationsvc.model.ContactMessage;
import org.notificationsvc.model.Notification;
import org.notificationsvc.model.NotificationPreference;
import org.notificationsvc.model.NotificationType;
import org.notificationsvc.web.dto.*;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class DtoMapper {

    // Mapping logic: прехвърляме един тип данни към друг
    public static NotificationType fromNotificationTypeRequest(NotificationTypeRequest dto) {

        return switch (dto) {
            case EMAIL -> NotificationType.EMAIL;
        };
    }

    // Build dto from entity
    public static NotificationPreferenceResponse fromNotificationPreference(NotificationPreference entity) {

        return NotificationPreferenceResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .contactInfo(entity.getContactInfo())
                .enabled(entity.isEnabled())
                .userId(entity.getUserId())
                .build();
    }

    public static NotificationResponse fromNotification(Notification entity) {

        // DTO building!
        return NotificationResponse.builder()
                .subject(entity.getSubject())
                .status(entity.getStatus())
                .createdOn(entity.getCreatedOn())
                .type(entity.getType())
                .build();
    }

    public static ContactMessageResponse fromContactMessage(ContactMessage entity) {
        return ContactMessageResponse.builder()
                .id(entity.getId()).
                userId(entity.getUserId())
                .email(entity.getEmail())
                .subject(entity.getSubject())
                .message(entity.getMessage())
                .createdAt(LocalDateTime.now())
                .build();
    }

//    public static ContactMessage fromContactRequest(ContactRequest dto) {
//        return ContactMessage.builder()
//                .name(dto.getName())
//                .email(dto.getEmail())
//                .subject(dto.getSubject())
//                .message(dto.getMessage())
//                .build();
//    }
}
