package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.entity.Supplier;

public class SupplierMock {

    public static Supplier getSupplier(){
        var supplier = new Supplier();
        supplier.setSupplierID(1);
        supplier.setScacCode("ABC");
        return supplier;
    }
}
