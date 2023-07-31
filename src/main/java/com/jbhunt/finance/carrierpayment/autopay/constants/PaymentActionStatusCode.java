package com.jbhunt.finance.carrierpayment.autopay.constants;

public enum PaymentActionStatusCode {

	CHARGECODEUPDATED("ChargeCodeUpdated"),CHARGEUPDATED("ChrgUpdt");

	private String actionStatusCode;

	PaymentActionStatusCode(String actionStatusCode) {
		this.actionStatusCode = actionStatusCode;
	}

	public String getActionStatusCode() {
		return actionStatusCode;
	}
}
