package org.notificationsvc.service;

import jakarta.transaction.Transactional;
import org.notificationsvc.repository.ContactRepository;
import org.notificationsvc.web.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.notificationsvc.model.ContactMessage;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public ContactService(ContactRepository contactRepository, JavaMailSender mailSender) {
        this.contactRepository = contactRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public ContactMessage saveMessage(ContactRequest contactRequest) {
        ContactMessage message = ContactMessage.builder()
                .userId(contactRequest.getUserId())
                .name(contactRequest.getUsername())
                .email(contactRequest.getEmail())
                .subject(contactRequest.getSubject())
                .message(contactRequest.getMessage())
                .build();
        contactRepository.save(message);
        return message;
    }

    public void sendEmail(ContactRequest contactRequest) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("paskalevdiyan@gmail.com");

        // mailMessage.setCc(contactRequest.getEmail()); if i want to sent a duplicate
        mailMessage.setSubject("New Contact Form Submission: " + contactRequest.getSubject());
        mailMessage.setText(
                "From: " + contactRequest.getUsername() + "\n" +
                        "Email: " + contactRequest.getEmail() + "\n\n" +
                        "Subject: " + contactRequest.getSubject()  + "\n" +
                        "Message: " + contactRequest.getMessage()
        );

        mailSender.send(mailMessage);
    }
}

