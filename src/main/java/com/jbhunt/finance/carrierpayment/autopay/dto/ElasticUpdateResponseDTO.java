package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;
@Data
public class ElasticUpdateResponseDTO {
    private Boolean created;
    private String result;
    private String index;
    private String type;
    private String id;
    private Integer version;
    private Boolean forcedRefresh;
    private ShardsDTO shards;
}
