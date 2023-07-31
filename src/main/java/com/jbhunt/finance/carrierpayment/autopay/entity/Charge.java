package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jbhunt.finance.carrierpayment.autopay.entity.converter.DateTimeAttributeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "CarrierPaymentCharge", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "carrierPaymentChargeId", scope = Charge.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class Charge extends AuditEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -455258576606234011L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarrierPaymentChargeID", unique = true, nullable = false)
    private Integer chargeId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "CarrierPaymentID", nullable = false)
    private Payment carrierPayment;

    @Column(name = "StopNumber")
    private Integer stopNumber;

    @Column(name = "ChargeCode", nullable = false, length = 10)
    private String chargeCode;

    @Column(name = "ChargeSourceTypeCode", nullable = false, length = 10)
    private String chargeSourceType;

    @Column(name = "ChargeUnitCode", length = 10)
    private String chargeUnitCode;

    @NotNull(message = "Charge Decision Person Id must be specified.")
    @Column(name = "ChargeDecisionUserID", nullable = false, length = 20)
    private String chargeDecisionPersonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CarrierInvoiceHeaderID", insertable = false, updatable = false)
    private CarrierInvoiceHeader carrierInvoiceHeader;

    @Column(name = "CarrierInvoiceHeaderID")
    private Integer headerId;

    @Column(name = "CarrierPaymentChargeDecisionCode", length = 10)
    private String chargeDecisionCode; // Approve,Reject,PAID

    @Column(name = "CarrierPaymentChargeDecisionReasonCode", length = 10)
    private String reasonCode;// rejection reason

    @Column(name = "ChargeUnitRateAmount", precision = 11, scale = 4)
    private BigDecimal chargeUnitRateAmount;

    @Column(name = "ChargeQuantity", nullable = false, precision = 11, scale = 4)
    private Double chargeQuantity;

    @Column(name = "TotalChargeAmount", precision = 11, scale = 4)
    @DecimalMax(value = "999999.99", message = "The decimal value can not be more than 999999.99")
    private BigDecimal totalChargeAmount;

    @Column(name = "ReferenceNumberTypeCode", length = 10)
    private String referenceNumberTypeCode;

    @Column(name = "ReferenceNumberValue", length = 30)
    private String referenceNumberValue;

    @Column(name = "ChargeDecisionDate")
    @Convert(converter = DateTimeAttributeConverter.class)
    private LocalDateTime chargeDecisionDate;

    @Column(name = "ChargeSentToAccountPayableDate")
    @Convert(converter = DateTimeAttributeConverter.class)
    private LocalDateTime sentToAPDate;

    @Column(name = "InvoiceNumberSuffix", length = 10)
    private String invoiceNumberSuffix;

    @Column(name = "CarrierQuickPayWaiverIndicator")
    private Character quickPayWaiverIndicator;

    @Column(name = "ChargeApplyToCustomerIndicator")
    private Character chargeApplyToCustomerIndicator;

    @Column(name = "ExternalChargeID")
    private Integer externalChargeID;

    @Column(name = "CarrierInvoiceNumberExtended", length = 40)
    private String carrierInvoiceNumberExtended;

    @Column(name = "ExpirationTimestamp")
    @Convert(converter = DateTimeAttributeConverter.class)
    private LocalDateTime expirationTimestamp;

    @Column(name = "CarrierPaymentChargeComment")
    private String rejectReasonComment;

    @Column(name = "TotalApprovedChargeAmount", precision = 11, scale = 4)
    private BigDecimal totalApprovedChargeAmount;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "charge", cascade = { CascadeType.ALL })
    private ChargeOverride chargeOverride;

    @Column(name = "ExternalChargeBillingID")
    private Integer externalChargeBillingID;

    @Column(name = "EstimatedDueDate")
    private LocalDate estimatedDueDate;

    @Column(name = "CarrierPaymentWorkflowStatusTypeCode")
    private String carrierPaymentWorkflowStatusTypeCode;

    @Column(name = "CarrierPaymentApprovalTransactionID")
    private Integer carrierApprovalTransaction;
}
