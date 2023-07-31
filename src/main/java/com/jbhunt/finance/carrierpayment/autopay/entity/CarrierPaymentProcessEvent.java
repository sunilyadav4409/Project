package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jbhunt.finance.carrierpayment.autopay.entity.converter.DateTimeAttributeConverter;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CarrierPaymentProcessEvent", schema = "CFP")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EntityListeners(AuditingEntityListener.class)
public class CarrierPaymentProcessEvent extends AuditEntity implements Serializable{

    private static final long serialVersionUID = 2326636769272538006L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarrierPaymentProcessEventID", unique = true, nullable = false)
    private Integer carrierPaymentProcessEventID;

    @Column(name = "CarrierPaymentID", nullable = false)
    private Integer carrierPaymentID;

    @Column(name = "CarrierPaymentWorkflowGroupTypeCode", nullable = false)
    private String carrierPaymentWorkflowGroupTypeCode;

    @Column(name="EventTypeCode", nullable = false)
    private String eventTypeCode;

    @Column(name="EventDatetime", nullable = false)
    @Convert(converter = DateTimeAttributeConverter.class)
    private LocalDateTime eventDatetime;

    @Column(name = "ProcessedTimestamp")
    @Convert(converter = DateTimeAttributeConverter.class)
    private LocalDateTime processedTimestamp;

    @Column(name="InvoiceSourceTypeCode")
    private String invoiceSourceTypeCode;

}
