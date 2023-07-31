package com.jbhunt.finance.carrierpayment.autopay.dto;


import lombok.Data;

@Data
public class PaymentEnrichmentDTO {

 private String originZip;
 private String destinationZip;
 private String billToCode;
 private String billToName;
 private String billToAddressLine1;
 private String billToAddressLine2;
 private String billToCityC;
 private String billToCityName;
 private String billToCityRandName;
 private String billToStateC;
 private String billToZip;
 private String equipmentType;
 private String proNumber;
 private String bolNumber;
 private String shipmentId;
 private String supplierName;
 private String addressLine1;
 private String addressLine2;
 private String addressLine3;
 private String city;
 private String stateCode;
 private String postalCode;
 private String countryCode;
 private String supplierId;
 private boolean factored;
 private String mcNumber;
 private String paymentTerms;
 private boolean quickPay;
 private String fleetCode;
 private int dispatchCount;
 private String autoRate;
}
