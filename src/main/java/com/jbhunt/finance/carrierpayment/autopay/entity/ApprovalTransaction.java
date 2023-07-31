package com.jbhunt.finance.carrierpayment.autopay.entity;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jbhunt.finance.carrierpayment.autopay.entity.converter.DateTimeAttributeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "CarrierPaymentApprovalTransaction", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "CarrierPaymentApprovalTransactionID", scope = ApprovalTransaction.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class ApprovalTransaction extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarrierPaymentApprovalTransactionID", unique = true, nullable = false)
    private Integer approvalTransactionID;

    @Column(name = "CarrierInvoiceHeaderID", nullable = false) // map to invoice header
    private Integer carrierInvoiceHeaderID;

    @Column(name = "CarrierPaymentID", nullable = false, length = 10)
    private Integer carrierPayment;

    @Column(name = "CarrierPaymentWorkflowStatusTypeCode", nullable = false, length = 10)
    private String carrierPaymentWorkflowStatusTypeCode;

    @Column(name = "EstimatedDueDate", nullable = false)
    private LocalDate estimatedDueDate;

    @Column(name = "RequirementCompletionTimestamp")
    @Convert(converter = DateTimeAttributeConverter.class)
    private LocalDateTime requirementCompletionTimestamp;

    @Column(name = "FactoringSupplierID")
    private Integer factoringSupplierID;

    @Column(name = "CarrierInvoiceNumberExtended", length = 80)
    private String carrierInvoiceNumberExtended;

    @Column(name = "DecisionDate")
    private LocalDate decisionDate;

    @Column(name = "DecisionUserID", length = 8)
    private String decisionUserID;

    @Column(name = "CarrierPaymentChargeDecisionCode", length = 10)
    private String carrierPaymentChargeDecisionCode;

    @Column(name = "CarrierPaymentChargeDecisionReasonCode", length = 10)
    private String carrierPaymentChargeDecisionReasonCode;

    @Column(name = "TransactionAmount", precision = 11, scale = 4)
    private BigDecimal transactionAmount;

    @Column(name = "CurrencyCode", length = 3)
    private String currencyCode;


}
