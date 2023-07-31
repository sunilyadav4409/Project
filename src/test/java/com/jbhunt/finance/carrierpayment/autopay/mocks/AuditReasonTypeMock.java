package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.entity.AuditReasonType;

public class AuditReasonTypeMock {

    public static AuditReasonType getAuditReasonType(String auditReason){
        AuditReasonType auditReasonType = new AuditReasonType();
        auditReasonType.setAuditReasonTypeName(auditReason);
        return auditReasonType;
    }
}
