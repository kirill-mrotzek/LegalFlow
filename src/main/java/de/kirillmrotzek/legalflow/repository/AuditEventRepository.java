package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
}
