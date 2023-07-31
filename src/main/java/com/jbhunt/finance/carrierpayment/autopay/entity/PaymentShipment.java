package com.jbhunt.finance.carrierpayment.autopay.entity;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "CarrierPaymentShipment", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "carrierPaymentShipmentID", scope = PaymentShipment.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)

//@EqualsAndHashCode(callSuper = false, exclude = "carrierPaymentLocationID")

public class PaymentShipment extends AuditEntity implements Serializable {

    private static final long serialVersionUID = -5740118257677679124L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarrierPaymentShipmentID", unique = true, nullable = false)
    private Integer carrierPaymentShipmentID;


    @Column(name = "LoadToken", nullable = false)
    private Integer loadToken;

    @Column(name = "LoadSource", nullable = false)
    private String loadSource;

    @Column(name = "LegacyLoadNumber", nullable = false)
    private String legacyLoadNumber;

    @Column(name = "FleetCode")
    private String fleetCode;

    @Column(name = "ShipmentType")
    private String shipmentType;

    @Column(name = "BilltoLocationID")
    private Integer billtoLocationID;

    @Column(name = "BranchCode")
    private String branchCode;

    @Column(name = "BranchName")
    private String branchName;

    @Column(name = "OriginLocationID")
    private Integer originLocationID;

    @Column(name = "DestinationLocationID")
    private Integer destinationLocationID;

    @Column(name = "DivisionCode")
    private String divisionCode;

}
