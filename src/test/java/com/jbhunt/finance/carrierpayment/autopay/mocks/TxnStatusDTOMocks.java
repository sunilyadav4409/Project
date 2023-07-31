package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;

public class TxnStatusDTOMocks {

    public static TxnStatusDTO createTxnStatusDTOMocks(){
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        txnStatusDTO.setName("Testing");
        txnStatusDTO.setSuccess(true);
        return txnStatusDTO;
    }

    public static TxnStatusDTO createTxnStatusDTOMocks(Boolean b){
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        txnStatusDTO.setName("Testing");
        txnStatusDTO.setSuccess(b);
        return txnStatusDTO;
    }
}
