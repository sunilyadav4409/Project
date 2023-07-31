package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jbhunt.finance.carrierpayment.autopay.entity.converter.DateTimeAttributeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false, exclude = "charge")
@Entity
@Table(name = "ChargeOverride", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "chargeOverrideId", scope = ChargeOverride.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
@Where(clause = "expirationTimestamp is null")
public class ChargeOverride extends AuditEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4297462976674323736L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ChargeOverrideID", unique = true, nullable = false)
    private Integer chargeOverrideId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CarrierPaymentChargeID", insertable = false, updatable = false)
    private Charge charge;

    @Column(name = "CarrierPaymentChargeID", nullable = false)
    private Integer chargeId;

    @Column(name = "OverrideApproverUserID", length = 20)
    private String overrideApproverId;

    @Column(name = "ChargeOverrideReasonCode", length = 10)
    private String overrideReasonCode;

    @Column(name = "ChargeDeviationReasonCode", length = 10)
    private String deviationReasonCode;

    @Column(name = "TotalOverrideChargeAmount", nullable = false, precision = 15, scale = 4)
    @DecimalMax(value = "999999.99", message = "The decimal value can not be more than 999999.99")
    private BigDecimal overrideAmount;

    @Column(name = "TotalVendorChargeAmount", nullable = false, precision = 15, scale = 4)
    private BigDecimal vendorAmount;

    @Column(name = "ChargeQuantity", nullable = false, precision = 11, scale = 4)
    private Double chargeQuantity;

    @Column(name = "OverrideReasonComment", nullable = true)
    private String reasonComment;

    @Column(name = "ExpirationTimestamp")
    @Convert(converter = DateTimeAttributeConverter.class)
    private LocalDateTime expirationTimestamp;
}
