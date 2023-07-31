package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jbhunt.finance.carrierpayment.autopay.entity.converter.DateTimeAttributeConverter;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.UserUtil;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import javax.persistence.*;




@MappedSuperclass
@Data
@EntityListeners(value = AuditingEntityListener.class)
public abstract class AuditEntity {

	private static final String CARRIER_PAYMENT_AUTO_PAY = "AutoPay";

	@Column(name = "CreateTimestamp", nullable = false)
	@JsonIgnore
	@Convert(converter = DateTimeAttributeConverter.class)
	private LocalDateTime crtS;

	@Column(name = "CreateUserID", nullable = false)
	@JsonIgnore
	private String crtUid;

	@Column(name = "CreateProgramName", nullable = false)
	@JsonIgnore
	private String crtPgmC;

	@Column(name = "LastUpdateTimestamp", nullable = false)
	@JsonIgnore
	private LocalDateTime lstUpdS;

	@Column(name = "LastUpdateUserID", nullable = false)
	@JsonIgnore
	private String lstUpdUid;

	@Column(name = "LastUpdateProgramName", nullable = false)
	@JsonIgnore
	private String lstUpdPgmC;

	@PrePersist

	public void prePersist() {
		String userId = (getUserName() != null && getUserName().length() > 8) ? getUserName().substring(0, 7) : getUserName();
		setCrtUid(userId);
		setCrtS(LocalDateTime.now());
		setCrtPgmC(CARRIER_PAYMENT_AUTO_PAY);
		setLstUpdUid(userId);
		setLstUpdS(LocalDateTime.now());
		setLstUpdPgmC(CARRIER_PAYMENT_AUTO_PAY);

	}


	@PreUpdate
	public void preUpdate() {
		String userId = (getUserName() != null && getUserName().length() > 8) ? getUserName().substring(0, 7) : getUserName();
		setLstUpdUid(userId);
		setLstUpdS(LocalDateTime.now());
		setLstUpdPgmC(CARRIER_PAYMENT_AUTO_PAY);
	}

	private String getUserName() {
		return UserUtil.getLoggedInUser();
	}



}
