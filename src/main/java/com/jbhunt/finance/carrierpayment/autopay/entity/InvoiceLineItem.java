package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "InvoiceLineItem", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "lineItemId", scope = InvoiceLineItem.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class InvoiceLineItem extends AuditEntity implements Serializable  {

	private static final long serialVersionUID = -5740118257677679124L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "InvoiceLineItemID", unique = true, nullable = false)
	private Integer lineItemId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CarrierInvoiceHeaderID", nullable = false)
	private CarrierInvoiceHeader carrierInvoiceHeader;
	
	@Column(name = "ItemClassCode")
	private String classCode;
	
	@Column(name = "ItemWeightQuantity")
	private Integer weight;
	
	@Column(name = "ItemPackageCount")
	private Integer packageCount;
	
	@Column(name = "UnitOfWeightMeasurementCode")
	private String unitOfWeightMeasurementCode;

	@Column(name = "PackagingUnitTypeCode")
	private String packageType;

	@Column(name = "ItemDescription")
	private String commodity;

	@Column(name = "ItemRateType")
	private String itemRateType;
	
	@Column(name = "HazardousMaterialIndicator")
	private String hazardousMaterialIndicator;
	
	@Column(name = "ItemRate")
	private BigDecimal itemRate;
	
	@Column(name = "ItemChargeAmount")
	private BigDecimal itemChargeAmount;
}
