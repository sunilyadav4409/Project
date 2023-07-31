package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jbhunt.finance.carrierpayment.autopay.entity.converter.DateTimeAttributeConverter;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "CarrierInvoiceHeader", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "carrierInvoiceHeaderId", scope = CarrierInvoiceHeader.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class CarrierInvoiceHeader extends AuditEntity implements Serializable {

	private static final long serialVersionUID = 8131926014192562233L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CarrierInvoiceHeaderID", unique = true, nullable = false)
	private Integer carrierInvoiceHeaderId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CarrierPaymentID", nullable = false)
	private Payment carrierPayment;

	@Column(name = "CarrierInvoiceNumber", length = 30)
	private String carrierInvoiceNumber;

	@Column(name = "SCACCode", nullable = false, length = 10)
	private String scacCode;

	@Column(name = "LoadNumber", nullable = false, length = 30)
	private String loadNumber;

	@Column(name = "DispatchNumber", nullable = false, length = 30)
	private String dispatchNumber;

	@Column(name = "InvoiceSourceTypeCode", nullable = false, length = 10)
	private String invoiceSourceTypeCode;

	@Column(name = "CarrierInvoiceStatusCode", nullable = false)
	private String invoiceStatusCode;

	@Column(name = "InvoiceDate")
	@Convert(converter = DateTimeAttributeConverter.class)
	private LocalDateTime invoiceDate;

	@Column(name = "CarrierDocumentNumber", nullable = false, length = 30)
	private String carrierDocumentNumber;

	@Column(name = "InvoiceReceivedDate", nullable = false)
	@Convert(converter = DateTimeAttributeConverter.class)
	private LocalDateTime invoiceReceivedDate;

	@Column(name = "PaymentDueDate", nullable = true)
	@Convert(converter = DateTimeAttributeConverter.class)
	private LocalDateTime paymentDueDate;

	@Column(name = "InvoiceRejectReasonCode", nullable = true)
	private String invoiceRejectReasonCode;

	@Column(name = "CarrierInvoiceHeaderComment", nullable = true)
	private String headerComment;

	@Column(name = "InvoiceOriginatorName", nullable = true)
	private String invoiceOriginatorName;

    @Column(name = "AutoApproveEligibileIndicator", nullable = true)
    private String autoApproveEligibleInd;

    @Column(name = "AutoApproveIneligibilityDescription", nullable = true)
    private String autoApproveIneligibiltyDesc;

	@Column(name = "TotalInvoiceAmount", nullable = true)
	private BigDecimal totalInvoiceAmount;

	@Column(name = "AutoApprovalDate", nullable = true)
	private LocalDateTime autoApprovalDate;

	@Column(name = "ScanTimestamp")
	@Convert(converter = DateTimeAttributeConverter.class)
	private LocalDateTime scanTimestamp;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "carrierInvoiceHeader", cascade = CascadeType.ALL)
	private List<CarrierInvoiceDetail> carrierInvoiceDetailList;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "carrierInvoiceHeader", cascade = CascadeType.ALL)
	private List<InvoiceLineItem> invoiceLineItems;

	@OneToMany(mappedBy = "carrierInvoiceHeader")
	private List<Charge> chargeList;

	//Added for Noloads columns Start

	@Column(name = "InvoiceTypeCode", nullable = false)
	private String invoiceTypeCode;

	@Column(name = "OrphanInvoiceStatusCode")
	private String orphanInvoiceStatusCode;

	@Column(name = "CarrierName")
	private String carrierName;

	@Column(name="CarrierPaymentWorkflowGroupTypeCode")
	private String carrierPaymentWorkflowGroupTypeCode;

	@Column(name = "ShipperName")
	private String shipperName;

	@Column(name = "ShipperAddress1")
	private String shipperAddress1;

	@Column(name = "ShipperAddress2")
	private String shipperAddress2;

	@Column(name = "ShipperCityName")
	private String shipperCityName;

	@Column(name = "ShipperStateName")
	private String shipperStateName;

	@Column(name = "ShipperPostalCode")
	private String shipperPostalCode;

	@Column(name="ShipperCountryCode")
	private String shipperCountryCode;

	@Column(name = "ReceiverName")
	private String receiverName;

	@Column(name = "ReceiverAddress1")
	private String receiverAddress1;

	@Column(name = "ReceiverAddress2")
	private String receiverAddress2;

	@Column(name = "ReceiverCityName")
	private String receiverCityName;

	@Column(name = "ReceiverStateName")
	private String receiverStateName;

	@Column(name = "ReceiverPostalCode")
	private String receiverPostalCode;

	@Column(name="ReceiverCountryCode")
	private String receiverCountryCode;

	@Column(name = "TotalWeightQuantity")
	private Integer totalWeightQuantity;

	@Column(name = "UnitOfWeightMeasurementCode")
	private String unitOfWeightMeasurementCode;

	@Column(name = "PickupDate")
	private LocalDate pickupDate;

	@Column(name = "DeliveryDate")
	private LocalDate deliveryDate;

	@Column(name = "CarrierPRONumber")
	private String carrierPRONumber;

	@Column(name="NMFCSCACCode")
	private String nmfcScac;

	@Column(name="ExternalBOLMatchIndicator", length = 1)
	private String externalBOLMatchIndicator;

	@Column(name = "CurrencyCode")
	private String currencyCode;

	//Added for Noloads columns End

}
