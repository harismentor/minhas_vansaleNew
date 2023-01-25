package com.advanced.minhas.model;

import java.io.Serializable;

public class Contra implements Serializable {

    private String Date;
    private String From_bank;
    private String To_bank;
    private String External_Voucher;
    private String Remarks;
    private String Amount;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFrom_bank() {
        return From_bank;
    }

    public void setFrom_bank(String from_bank) {
        From_bank = from_bank;
    }

    public String getTo_bank() {
        return To_bank;
    }

    public void setTo_bank(String to_bank) {
        To_bank = to_bank;
    }

    public String getExternal_Voucher() {
        return External_Voucher;
    }

    public void setExternal_Voucher(String external_Voucher) {
        External_Voucher = external_Voucher;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }


}
