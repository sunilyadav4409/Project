package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jbhunt.finance.carrierpayment.autopay.entity.converter.DateTimeAttributeConverter;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CarrierPaymentActivityTrack", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "activityTrackId", scope = Activity.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class Activity extends AuditEntity implements Serializable {

	private static final long serialVersionUID = 7338653920524164407L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CarrierPaymentActivityTrackID", unique = true, nullable = false)
	private int activityTrackId;

	@Column(name = "CarrierPaymentActivityTypeCode", nullable = false)
	private String activityTypeCode;

	@Column(name = "CarrierPaymentActivityPerformTypeCode", nullable = false)
	private String activityPerformTypeCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CarrierPaymentID")
	private Payment payment;

	@Column(name = "CarrierPaymentActivitySourceTypeCode", nullable = false)
	private String activitySourceTypeCode;

	@Column(name = "PowerFleetCode")
	private String powerFleetCode;

	@Column(name = "CarrierPaymentActivityPerformerID", nullable = false)
	private String activityPerformerId;

	@Column(name = "CarrierPaymentActivityDate", nullable = false)
	@Convert(converter = DateTimeAttributeConverter.class)
	private LocalDateTime activityDate;

	@Column(name = "ActivityDetailTypeCode", nullable = true)
	private String activityDetailTypeCode;

	@Column(name="ActivityDetailReferenceValue", nullable = true)
	private String activityDetailReferenceValue;

}
