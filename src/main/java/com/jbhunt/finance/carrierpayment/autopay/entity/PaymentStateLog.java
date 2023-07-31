package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jbhunt.finance.carrierpayment.autopay.entity.converter.DateTimeAttributeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "PaymentStateLog", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "paymentStateLogId", scope = PaymentStateLog.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class PaymentStateLog extends AuditEntity implements Serializable {
   private static final long serialVersionUID = 1L;
@Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "PaymentStateLogId", unique = true, nullable = false)
   private Long paymentStateLogId;
@ManyToOne
   @JoinColumn(name = "CarrierPaymentID", unique = true, nullable = false)
   private Payment carrierPaymentId;
@Column(name = "CarrierPaymentWorkflowStatusTypeCode",nullable = false)
   private String statusFlowTypeCode;
@Column(name = "PaymentActionTypeCode")
   private String paymentActionTypeCode;

   @Column(name = "PaymentStateStartTimestamp")
   @Convert(converter = DateTimeAttributeConverter.class)
   private LocalDateTime paymentTaskStartDate;

@Column(name = "PaymentActionReasonCode")
   private String reasonCode;

  @Column(name = "PaymentStateEndTimestamp")
  @Convert(converter = DateTimeAttributeConverter.class)
   private LocalDateTime paymentTaskEndDate;

@Column(name = "PaymentActionComment")
   private String paymentActionComment;
@ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "CarrierPaymentWorkflowStatusReasonTypeCode")
   private CarrierPaymentWorkflowStatusReasonType carrierPaymentWorkflowStatusReasonTypeCode;
}
