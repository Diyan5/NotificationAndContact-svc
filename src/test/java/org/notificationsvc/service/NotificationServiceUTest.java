package org.notificationsvc.service;

import org.notificationsvc.model.NotificationPreference;
import org.notificationsvc.model.NotificationType;
import org.notificationsvc.repository.NotificationPreferenceRepository;
import org.notificationsvc.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.notificationsvc.web.dto.NotificationTypeRequest;
import org.notificationsvc.web.dto.UpsertNotificationPreference;
import org.springframework.mail.MailSender;
import org.notificationsvc.model.Notification;
import org.notificationsvc.model.NotificationStatus;
import org.notificationsvc.web.dto.NotificationRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceUTest {

    @Mock
    private NotificationPreferenceRepository preferenceRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private MailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;



    @Test
    void givenNotExistingNotificationPreference_whenChangeNotificationPreference_thenExpectException(){

        // Given
        UUID userId = UUID.randomUUID();
        boolean isNotificationEnabled = true;
        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NullPointerException.class, () -> notificationService.changeNotificationPreference(userId, isNotificationEnabled));
    }

    @Test
    void givenExistingNotificationPreference_whenChangeNotificationPreference_thenExpectEnabledToBeChanged(){

        // Given
        UUID userId = UUID.randomUUID();
        NotificationPreference preference = NotificationPreference.builder()
                .enabled(false)
                .build();
        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(preference));

        // When
        notificationService.changeNotificationPreference(userId, true);

        // Then
        assertTrue(preference.isEnabled());
        verify(preferenceRepository, times(1)).save(preference);
    }

    @Test
    void givenExistingPreference_whenUpsertPreference_thenUpdateExisting() {
        // Given
        UUID userId = UUID.randomUUID();
        UpsertNotificationPreference dto = new UpsertNotificationPreference();
        dto.setUserId(userId);
        dto.setContactInfo("test@example.com");
        dto.setNotificationEnabled(true);
        dto.setType(NotificationTypeRequest.EMAIL); // използвай реален enum

        NotificationPreference existingPreference = NotificationPreference.builder()
                .userId(userId)
                .contactInfo("old@example.com")
                .enabled(false)
                .type(NotificationType.EMAIL)
                .createdOn(LocalDateTime.now().minusDays(1))
                .updatedOn(LocalDateTime.now().minusDays(1))
                .build();

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(existingPreference));
        when(preferenceRepository.save(any(NotificationPreference.class))).thenAnswer(i -> i.getArgument(0));

        // When
        NotificationPreference result = notificationService.upsertPreference(dto);

        // Then
        assertTrue(result.isEnabled());
        assertTrue(result.getContactInfo().equals("test@example.com"));
        assertEquals(NotificationType.EMAIL, result.getType());
        verify(preferenceRepository, times(1)).save(existingPreference);
    }

    @Test
    void givenNoExistingPreference_whenUpsertPreference_thenCreateNew() {
        // Given
        UUID userId = UUID.randomUUID();
        UpsertNotificationPreference dto = new UpsertNotificationPreference();
        dto.setUserId(userId);
        dto.setContactInfo("new@example.com");
        dto.setNotificationEnabled(true);
        dto.setType(NotificationTypeRequest.EMAIL);

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(NotificationPreference.class))).thenAnswer(i -> i.getArgument(0));

        // When
        NotificationPreference result = notificationService.upsertPreference(dto);

        // Then
        assertEquals(userId, result.getUserId());
        assertEquals("new@example.com", result.getContactInfo());
        assertTrue(result.isEnabled());
        assertEquals(NotificationType.EMAIL, result.getType());
        verify(preferenceRepository, times(1)).save(any(NotificationPreference.class));
    }

    @Test
    void givenDisabledNotificationPreference_whenSendNotification_thenThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setSubject("Test Subject");
        request.setBody("Test Body");

        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId)
                .contactInfo("user@example.com")
                .enabled(false)
                .type(NotificationType.EMAIL)
                .build();

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(preference));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationService.sendNotification(request));

        assertTrue(exception.getMessage().contains(userId.toString()));
    }

    @Test
    void givenValidNotificationRequest_whenSendNotification_thenSaveSucceededNotification() {
        // Given
        UUID userId = UUID.randomUUID();
        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setSubject("Test Subject");
        request.setBody("Test Body");

        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId)
                .contactInfo("user@example.com")
                .enabled(true)
                .type(NotificationType.EMAIL)
                .build();

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(preference));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Notification result = notificationService.sendNotification(request);

        // Then
        assertEquals(NotificationStatus.SUCCEEDED, result.getStatus());
        assertEquals("Test Subject", result.getSubject());
        assertEquals("Test Body", result.getBody());
        assertEquals(userId, result.getUserId());

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void givenMailException_whenSendNotification_thenSaveFailedNotification() {
        // Given
        UUID userId = UUID.randomUUID();
        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setSubject("Test Subject");
        request.setBody("Test Body");

        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId)
                .contactInfo("user@example.com")
                .enabled(true)
                .type(NotificationType.EMAIL)
                .build();

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(preference));
        doThrow(new MailException("SMTP error") {}).when(mailSender).send(any(SimpleMailMessage.class));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Notification result = notificationService.sendNotification(request);

        // Then
        assertEquals(NotificationStatus.FAILED, result.getStatus());
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

}
