package com.jbhunt.finance.carrierpayment.autopay.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "AuditReasonType", schema = "CFP")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AuditReasonType extends AuditEntity {
    @Id
    @Column(name = "AuditReasonTypeCode", nullable = false)
    private String auditReasonTypeName;
}

