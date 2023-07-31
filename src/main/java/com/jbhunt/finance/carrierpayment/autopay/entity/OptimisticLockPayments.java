package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jbhunt.finance.carrierpayment.autopay.entity.converter.DateTimeAttributeConverter;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CarrierPaymentOptimisticLock", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "carrierPaymentOptimisticLockId", scope = OptimisticLockPayments.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class OptimisticLockPayments extends AuditEntity implements Serializable {

    private static final long serialVersionUID = 8131926014192562233L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarrierPaymentOptimisticLockID", unique = true, nullable = false)
    private Integer carrierPaymentOptimisticLockId;

    @Column(name = "CarrierPaymentID", nullable = false)
    private Integer carrierPaymentId;

    @Column(name = "LockUserID", nullable = false, length = 30)
    private String lockUserId;

    @Column(name = "LockActionTypeCode", nullable = false)
    private String lockActionTypeCode;

    @Column(name = "EffectiveTimestamp", nullable = false)
    @Convert(converter = DateTimeAttributeConverter.class)
    private LocalDateTime effectiveTimestamp;

    @Column(name = "ExpirationTimestamp")
    @Convert(converter = DateTimeAttributeConverter.class)
    private LocalDateTime expirationTimestamp;
}
