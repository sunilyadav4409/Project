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
@Table(name = "CarrierPayment", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "carrierPaymentId", scope = Payment.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class Payment extends AuditEntity implements Serializable {

	private static final long serialVersionUID = -5740118257677679124L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CarrierPaymentID", unique = true, nullable = false)
	private Integer carrierPaymentId;

	@Column(name = "SCACCode", nullable = false)
	private String scacCode;

	@Column(name = "LoadNumber", nullable = false)
	private String loadNumber;

	@Column(name = "DispatchNumber", nullable = false)
	private String dispatchNumber;

	@Column(name = "TransitModeCode")
	private String transitMode;

	@Column(name = "ReceiverLocationCode")
	private String recieverLocationId;

	@Column(name = "BillingPartyCode")
	private String billToPartyId;

	@Column(name = "ShipperLocationCode")
	private String shipperLocationId;

	@Column(name = "PaymentMethodTypeCode", nullable = false)
	private String paymentMethodTypeCode;

	@Column(name = "CarrierPaymentWorkflowGroupTypeCode", nullable = false)
	private String groupFlowTypeCode;

	@Column(name = "CarrierPaymentWorkflowStatusTypeCode", nullable = false)
	private String statusFlowTypeCode;

	@Column(name = "PaymentActionTypeCode", nullable = false)
	private String paymentActionTypeCode;

	@Column(name = "DispatchTokenID")
	private String dispatchTokenId;

	@Column(name = "DispatchTokenYear")
	private Integer dispatchYear;

	@Column(name = "ProjectCode")
	private String projectCode;
	
	@Column(name = "TotalApprovedInvoiceAmount")
	private BigDecimal totalApprovedInvoiceAmt;

	@Column(name = "TotalChargeAmount")
	private BigDecimal totalChargeAmount;
	
	@Column(name = "DispatchCompletionDate")
	private LocalDate dispatchCompletionDate;

	@Column(name = "CarrierPaymentDate")
	@Convert(converter = DateTimeAttributeConverter.class)
	private LocalDateTime carrierPaymentDate;

	@Column(name = "VendorPaymentTerm")
	private Integer vendorPaymentTerm;

	@Column(name = "EstimatedCarrierPaymentDueDate")
	private LocalDate estimatedDueDate;

	@Column(name = "LoadTypeCode")
	private String loadTypeCode;

	@Column(name = "FleetCode")
	private String fleetCode;

	@Column(name = "DispatchStatus")
	private String dispatchStatus;

	@Column(name = "FinalDeliveryAppointmentDate")
	private LocalDate appointmentDate;


	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "CarrierPaymentShipmentID", insertable = false, updatable = false)
	private PaymentShipment carrierPaymentShipmentID;

	@Column(name = "CurrencyCode")
	private String currencyCode;

	@Column(name = "DispatchMiles")
	private Integer dispatchMiles;

	@Column(name = "PowerEquipmentNumber")
	private String powerEquipmentNumber;

	@Column(name = "DispatchDestinationLocationID")
	private Integer dispatchDestinationLocationID;

	@Column(name = "DispatchOriginLocationID")
	private Integer dispatchOriginLocationID;

	@Column(name = "DispatchDate")
	private LocalDate dispatchDate;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "carrierPayment", cascade=CascadeType.ALL) 
	private List<CarrierInvoiceHeader> carrierInvoiceHeaderList;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "carrierPayment",cascade=CascadeType.ALL)
	private List<Charge> carrierPaymentChargeList;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "carrierPaymentId",cascade=CascadeType.ALL)
	private List<PaymentStateLog> paymentStateLog;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paymentId",cascade=CascadeType.ALL)
	private List<RequiredDocument> requiredDocuments;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "payment",cascade=CascadeType.ALL)
	private List<Activity> activities;
}
