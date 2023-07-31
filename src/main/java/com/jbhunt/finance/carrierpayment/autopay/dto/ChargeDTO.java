package com.jbhunt.finance.carrierpayment.autopay.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ChargeDTO {

    private Integer chargeId;
    private Integer paymentId;
    private Integer headerId;
    private Integer stopNumber;
    private Integer previousStop;
    private String chargeCode;
    private String chargeSourceType;
    private String chargeUnitCode;
    private BigDecimal chargeUnitRateAmount;
    private BigDecimal totalChargeAmount;
    private Double chargeQuantity;
    private String invoiceNumber;
    private String invoiceNumberSuffix;
    private String invoiceDate;
    private String chargeDecisionPersonId;
    private LocalDateTime chargeDecisionDate;
    private String chargeDecisionCode;
    private String reasonCode;
    private String referenceNumberTypeCode;
    private String referenceNumberValue;
    private Boolean chargeOverrideFlag;
    private ChargeOverrideDTO chargeOverride;
    private Character chargeApplyToCustomerIndicator = 'N';
    private String loadNumber;
    private String dispatchNumber;
    private String scacCode;
    private String customerChargeCode;
    private String workFlowGroupType;
    private String userRole;
    private boolean revokeWarning = false;
    private boolean overrideWarningSave = false;
    private boolean noWarning = false;
    private List<Integer> chargeIdList = new ArrayList<>();
    private List<Integer> externalChargeIDList = new ArrayList<>();
    private LocalDateTime updatedTime;
    private List<LocalDateTime> updatedTimeList;
    private LocalDateTime lastModified;
    private List<LocalDateTime> lastModifiedList = new ArrayList<>();
    private LocalDateTime paymentTimeStamp;
    private boolean isAutoPay = false;
    private Integer externalChargeID;
    private String billToCode;
    private Character quickPayWaiverIndicator;

    private boolean amountDiffer = false;
    private String carrierDocumentNumber;
    private LocalDateTime invoiceReceivedDate;
    private String reasonCodeDescription;

    private String settlementNumber;
    private LocalDateTime settlementDate;

    private String rejectReasonComment;
    private boolean nonAmountChange = false;

    private String projectCode;
    private List<ChargeAuditDTO> chargeAuditDTOs = new ArrayList<>();
    private Integer jobId;


    private Integer rerateChargeId;
    private boolean revokeLumperDocumentWarning = false;
    private Integer externalChargeBillingID;
    private BigDecimal totalApprovedChargeAmount;

    private LocalDateTime invoiceReceivedTermsDate;

    private LocalDate estimatedDueDate;
    private String workFlowStatus;
    private Map<Integer,String> chargeCodeList;
    private Integer orderID;
    private String currencyCode;

}
