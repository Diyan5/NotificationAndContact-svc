package org.notificationsvc.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.notificationsvc.model.ContactMessage;
import org.notificationsvc.repository.ContactRepository;
import org.notificationsvc.web.dto.ContactRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ContactServiceUTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ContactService contactService;

    private ContactRequest contactRequest;
    private ContactMessage contactMessage;

    @BeforeEach
    void setUp() {
        contactRequest = new ContactRequest();
        contactRequest.setUserId(UUID.randomUUID());
        contactRequest.setUsername("Test User");
        contactRequest.setEmail("test@example.com");
        contactRequest.setSubject("Test Subject");
        contactRequest.setMessage("This is a test message.");

        contactMessage = ContactMessage.builder()
                .userId(contactRequest.getUserId())
                .name(contactRequest.getUsername())
                .email(contactRequest.getEmail())
                .subject(contactRequest.getSubject())
                .message(contactRequest.getMessage())
                .build();
    }

    @Test
    void saveMessage_ShouldSaveToRepository() {
        when(contactRepository.save(any(ContactMessage.class))).thenReturn(contactMessage);

        ContactMessage savedMessage = contactService.saveMessage(contactRequest);

        assertNotNull(savedMessage);
        assertEquals(contactRequest.getUserId(), savedMessage.getUserId());
        assertEquals(contactRequest.getUsername(), savedMessage.getName());
        assertEquals(contactRequest.getEmail(), savedMessage.getEmail());
        assertEquals(contactRequest.getSubject(), savedMessage.getSubject());
        assertEquals(contactRequest.getMessage(), savedMessage.getMessage());
        verify(contactRepository, times(1)).save(any(ContactMessage.class));
    }

    @Test
    void sendEmail_ShouldSendMail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        contactService.sendEmail(contactRequest);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}

