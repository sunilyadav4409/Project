package com.jbhunt.finance.carrierpayment.autopay.dto;


import lombok.Data;

@Data
public class ShardsDTO {
	private Integer successful;
	private Integer failed;
	private Integer total;
}
