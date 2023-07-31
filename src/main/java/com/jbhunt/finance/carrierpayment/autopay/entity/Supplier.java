package com.jbhunt.finance.carrierpayment.autopay.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Supplier", schema = "CFP")
@EqualsAndHashCode(callSuper = true)
public class Supplier extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SupplierID", unique = true, nullable = false)
    private Integer supplierID;

    @Column(name = "SCACCode", nullable = true)
    private String scacCode;

    @Column(name = "CarrierName", nullable = false)
    private String carrierName;

    @Column(name = "ExternalSupplierID")
    private String externalSupplierID;

    @Column(name = "FactoringCompanyID")
    private Integer factoringCompanyID;

    @Column(name = "SupplierStatusCode")
    private String supplierStatusCode;

    @Column(name = "SupplierStatusReason")
    private  String statusReason;

    @Column(name = "SupplierStatusComment")
    private String statusReasonDescription;

    @Column(name = "Address1", nullable = false)
    private String address1;

    @Column(name = "Address2")
    private String address2;

    @Column(name = "CityName", nullable = false)
    private String cityName;

    @Column(name = "StateName", nullable = false)
    private String stateName;

    @Column(name = "PostalCode", nullable = false)
    private String postalCode;

    @Column(name = "CountryCode", nullable = false)
    private String countryCode;

    @Column(name = "PaymentMethodTypeCode", nullable = false)
    private String paymentMethodtypeCode;

    @Column(name = "PaymentTerms", nullable = false)
    private Integer paymentTerms;

    @Column(name = "EffectiveTimestamp", nullable = false)
    private LocalDateTime effectiveTimestamp;

    @Column(name = "ExpirationTimestamp", nullable = false)
    private LocalDateTime expirationTimestamp;


    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "FAXNumber")
    private String faxNumber;

    @Column(name = "EmailAddress")
    private String emailAddress;

    @Column(name = "MCNumber")
    private String mcNumber;

    @Column(name = "DOTNumber")
    private String dotNumber;

    @Column(name = "CarrierID")
    private Integer carrierId;

    @Column(name = "SupplierCategoryTypeCode")
    private String supplierCategory;

    @Column(name = "SettlementDeliverMethodTypeCode")
    private String settlementDeliverMethod;

    @Column(name = "CurrencyCode")
    private String defaultCurrency;

    @Column(name = "SupplierAliasName")
    private String factoringCompanyAliasName;

    @Column(name = "ParentSupplierID")
    private Integer parentSupplierID;

}


