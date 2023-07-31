package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.entity.converter.DateTimeAttributeConverter;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public class ChargeAuditEntity {


    private static final String CARRIER_PAYMENT_AUTO_PAY = "AutoPay";
    private static final String CARRIER_PAYMENT = "Carrier Payment";

    @Column(name = "CreateTimestamp", nullable = false)
    @JsonIgnore
    @Convert(converter = DateTimeAttributeConverter.class)
    private LocalDateTime crtS;

    @Column(name = "CreateUserID", nullable = false)
    @JsonIgnore
    private String crtUid;

    @Column(name = "CreateProgramName", nullable = false)
    @JsonIgnore
    private String crtPgmC;

    @Column(name = "SourceProgramName", nullable = false)
    @JsonIgnore
    private String sourcePrgrmName;

    @Column(name = "SourceUserId", nullable = false)
    @JsonIgnore
    private String sourceUserId;


    @PrePersist
    public void prePersist() {
        setCrtUid(CarrierPaymentConstants.SYSTEM_USERID);
        setCrtS(LocalDateTime.now());
        setCrtPgmC(CARRIER_PAYMENT_AUTO_PAY);
        setSourcePrgrmName(CARRIER_PAYMENT);
        setSourceUserId(CarrierPaymentConstants.SYSTEM_USERID);

    }
    



}
