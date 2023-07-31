package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CarrierPaymentWorkflowStatusReasonType", schema = "CFP")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class CarrierPaymentWorkflowStatusReasonType extends AuditEntity implements Serializable {


    private static final long serialVersionUID = 7165613456083649866L;
    @Id
    @Column(name = "CarrierPaymentWorkflowStatusReasonTypeCode", unique = true, nullable = false)
    private String carrierPaymentWorkflowStatusReasonTypeCode;

    @Column(name = "CarrierPaymentWorkflowStatusReasonTypeDescription")
    private String carrierPaymentWorkflowStatusReasonTypeDes;

    @Column(name = "EffectiveTimestamp", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "ExpirationTimestamp")
    private LocalDateTime expirationDate;
}
