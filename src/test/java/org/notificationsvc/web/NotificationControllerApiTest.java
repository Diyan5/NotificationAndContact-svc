package org.notificationsvc.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.notificationsvc.model.*;
import org.notificationsvc.service.ContactService;
import org.notificationsvc.service.NotificationService;
import org.notificationsvc.web.dto.ContactRequest;
import org.notificationsvc.web.dto.NotificationRequest;
import org.notificationsvc.web.dto.UpsertNotificationPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private ContactService contactService;

    @Test
    void upsertNotificationPreference_shouldReturnCreatedStatusAndResponse() throws Exception {
        UUID userId = UUID.randomUUID();
        UpsertNotificationPreference request = new UpsertNotificationPreference();
        request.setUserId(userId);
        request.setNotificationEnabled(true);

        NotificationPreference preference = new NotificationPreference();
        preference.setUserId(userId);
        preference.setEnabled(true);

        when(notificationService.upsertPreference(request)).thenReturn(preference);

        mockMvc.perform(post("/api/v1/notifications/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void getUserNotificationPreference_shouldReturnPreference() throws Exception {
        UUID userId = UUID.randomUUID();

        // Create false NotificationPreference
        NotificationPreference preference = NotificationPreference.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .enabled(true)
                .type(NotificationType.EMAIL)
                .contactInfo("test@example.com")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        when(notificationService.getPreferenceByUserId(userId)).thenReturn(preference);

        mockMvc.perform(get("/api/v1/notifications/preferences")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.type").value("EMAIL"))
                .andExpect(jsonPath("$.contactInfo").value("test@example.com"));
    }

    @Test
    void sendNotification_shouldReturnCreatedResponse() throws Exception {
        UUID userId = UUID.randomUUID();

        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setSubject("Test Subject");
        request.setBody("Test Body");

        Notification mockNotification = Notification.builder()
                .subject("Test Subject")
                .body("Test Body")
                .status(NotificationStatus.SUCCEEDED)
                .type(NotificationType.EMAIL)
                .createdOn(LocalDateTime.now())
                .build();

        when(notificationService.sendNotification(request)).thenReturn(mockNotification);

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subject").value("Test Subject"))
                .andExpect(jsonPath("$.status").value("SUCCEEDED"))
                .andExpect(jsonPath("$.type").value("EMAIL"))
                .andExpect(jsonPath("$.createdOn").exists());
    }

    @Test
    void changeNotificationPreference_shouldReturnUpdatedPreference() throws Exception {
        UUID userId = UUID.randomUUID();

        NotificationPreference preference = NotificationPreference.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .enabled(false)
                .type(NotificationType.EMAIL)
                .contactInfo("test@example.com")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        when(notificationService.changeNotificationPreference(userId, false)).thenReturn(preference);

        mockMvc.perform(put("/api/v1/notifications/preferences")
                        .param("userId", userId.toString())
                        .param("enabled", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.contactInfo").value("test@example.com"))
                .andExpect(jsonPath("$.type").value("EMAIL"));
    }

    @Test
    void createContactMessage_shouldReturnCreatedResponse() throws Exception {
        ContactRequest request = new ContactRequest();
        request.setEmail("test@example.com");
        request.setSubject("Test subject");
        request.setMessage("This is a test message.");

        ContactMessage contactMessage = ContactMessage.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .subject("Test subject")
                .message("This is a test message.")
                .build();

        when(contactService.saveMessage(any(ContactRequest.class))).thenReturn(contactMessage);
        doNothing().when(contactService).sendEmail(any(ContactRequest.class));

        mockMvc.perform(post("/api/v1/notifications/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.subject").value("Test subject"))
                .andExpect(jsonPath("$.message").value("This is a test message."));
    }
}
