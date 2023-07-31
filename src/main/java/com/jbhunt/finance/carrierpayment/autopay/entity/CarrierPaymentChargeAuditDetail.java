package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CarrierPaymentChargeAuditDetail", schema = "CFP")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CarrierPaymentChargeAuditDetail extends ChargeAuditEntity{

    @Id
    @GeneratedValue
    @Column(name = "CarrierPaymentChargeAuditDetailID", unique = true, nullable = false)
    private Integer carrierPaymentChargeAuditID;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "CarrierPaymentID", nullable = false)
    private Payment carrierPayment;

    @Column(name = "ChargeCode", nullable = false)
    private String chargeCode;

    @Column(name = "ChargeAmount", precision = 11, scale = 4)
    @DecimalMax(value = "999999.99", message = "The decimal value can not be more than 999999.99")
    private BigDecimal totalChargeAmount;

    @Column(name = "CarrierPaymentChargeID")
    private Integer chargeId;

    @Column(name = "originalChargeIndicator",nullable = false)
    private Character originalChargeIndicator;

    @Column(name = "ExpirationTimestamp")
    private LocalDateTime expirationTimestamp;

    @Column(name = "StopNumber")
    private Integer stopNumber;

    @Column(name="CurrencyCode")
    private String currencyCode;

    @Column(name = "SourceProgramName", nullable = false)
    @JsonIgnore
    private String sourcePrgrmName;

    @Column(name = "SourceUserId", nullable = false)
    @JsonIgnore
    private String sourceUserId;

    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "AuditReasonTypeCode")
    private AuditReasonType auditReason;

    @Column(name = "ChargeCreationTimeTypeCode")
    private String chargeCreationTimeTypeCode;
}
