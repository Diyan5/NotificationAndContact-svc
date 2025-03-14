package org.notificationsvc.web;

import jakarta.validation.Valid;
import org.notificationsvc.model.ContactMessage;
import org.notificationsvc.model.Notification;
import org.notificationsvc.model.NotificationPreference;
import org.notificationsvc.service.ContactService;
import org.notificationsvc.service.NotificationService;
import org.notificationsvc.web.dto.*;
import org.notificationsvc.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final ContactService contactService;

    @Autowired
    public NotificationController(NotificationService notificationService, ContactService contactService) {
        this.notificationService = notificationService;
        this.contactService = contactService;
    }

    @PostMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> upsertNotificationPreference(@RequestBody UpsertNotificationPreference upsertNotificationPreference) {

        NotificationPreference notificationPreference = notificationService.upsertPreference(upsertNotificationPreference);

        NotificationPreferenceResponse responseDto = DtoMapper.fromNotificationPreference(notificationPreference);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> getUserNotificationPreference(@RequestParam(name = "userId") UUID userId) {

        NotificationPreference notificationPreference = notificationService.getPreferenceByUserId(userId);

        NotificationPreferenceResponse responseDto = DtoMapper.fromNotificationPreference(notificationPreference);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest notificationRequest) {

        // Entity
        Notification notification = notificationService.sendNotification(notificationRequest);

        // DTO
        NotificationResponse response = DtoMapper.fromNotification(notification);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotificationHistory(@RequestParam(name = "userId") UUID userId) {

        List<NotificationResponse> notificationHistory = notificationService.getNotificationHistory(userId)
                .stream()
                .sorted(Comparator.comparing(Notification::getCreatedOn).reversed()) // Сортиране на ентититата преди мапване
                .map(DtoMapper::fromNotification)
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationHistory);
    }

    @PutMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> changeNotificationPreference(@RequestParam(name = "userId") UUID userId, @RequestParam(name = "enabled") boolean enabled) {

        NotificationPreference notificationPreference = notificationService.changeNotificationPreference(userId, enabled);

        NotificationPreferenceResponse responseDto = DtoMapper.fromNotificationPreference(notificationPreference);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    // DELETE /api/v1/notifications
    @DeleteMapping
    public ResponseEntity<Void> clearNotificationHistory(@RequestParam(name = "userId") UUID userId) {

        notificationService.clearNotifications(userId);

        return ResponseEntity.ok().body(null);
    }

    //  Endpoint: GET /api/v1/notifications/test  = "Hello, unknown user!"
    @GetMapping("/test")
    public ResponseEntity<String> getHelloWorld(@RequestParam(name = "name") String name) {

        return ResponseEntity.ok("Hello, " + name + " user!");
    }

    @PutMapping
    public ResponseEntity<Void> retryFailedNotifications(@RequestParam(name = "userId") UUID userId) {

        notificationService.retryFailedNotifications(userId);

        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/contact")
    public ResponseEntity<ContactMessageResponse> createContactMessage(
            @Valid @RequestBody ContactRequest request
    ) {
        // Запазваме съобщението в базата
        ContactMessage message = contactService.saveMessage(request);

        // Изпращаме имейл уведомление
        contactService.sendEmail(request);

        // Връщаме DTO с данни за съобщението
        ContactMessageResponse response = DtoMapper.fromContactMessage(message);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
