package org.notificationsvc.repository;

import org.notificationsvc.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<ContactMessage, UUID> {
}
