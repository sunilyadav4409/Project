package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.AuditReasonType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditReasonTypeRepository  extends JpaRepository<AuditReasonType, String> {

    AuditReasonType findByAuditReasonTypeName(String auditReasonType);
}
