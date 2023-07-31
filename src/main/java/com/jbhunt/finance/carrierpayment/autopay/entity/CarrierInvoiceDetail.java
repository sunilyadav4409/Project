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
@Table(name = "CarrierInvoiceDetail", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "carrierInvoiceDetailId", scope = CarrierInvoiceDetail.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class CarrierInvoiceDetail extends AuditEntity implements Serializable {

	private static final long serialVersionUID = -7787484051100508501L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CarrierInvoiceDetailID", unique = true, nullable = false)
	private Integer carrierInvoiceDetailId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CarrierInvoiceHeaderID", nullable = false)
	private CarrierInvoiceHeader carrierInvoiceHeader;

	@Column(name = "ChargeCode", nullable = false, length = 10)
	private String chargeCode;

	@Column(name = "ChargeUnitCode", nullable = false, length = 10)
	private String chargeUnitCode;

	@Column(name = "CarrierChargeUnitRateAmount", precision = 11, scale = 4)
	private BigDecimal carrierChargeUnitRateAmount;

	@Column(name = "ChargeQuantity", nullable = false)
	private Integer chargeQuantity;
}
