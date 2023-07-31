package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PaymentMethodType", schema = "CFP")
//@JsonIdentityInfo(generator =ObjectIdGenerators.IntSequenceGenerator.class, property="@id", scope = PaymentMethodType.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class PaymentMethodType {

	@Id
	@Column(name = "PaymentMethodTypeCode", nullable = false)
	private String paymentMethodTypeCode;

	@Column(name = "PaymentMethodTypeDescription", nullable = false)
	private String paymentMethodTypeCodeDesc;

	@Column(name = "EffectiveTimestamp", nullable = false)
	private LocalDateTime effDate;

	@Column(name = "ExpirationTimestamp", nullable = false)
	private LocalDateTime expDate;

	@Column(name = "SortSequence", nullable = false)
	private Integer sortSequence;
}
