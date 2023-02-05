package com.example.myapplication;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EinkaufslistProdukt  extends Produkt implements Checkable {

    String insertDate;
    String checkStatus;

    public EinkaufslistProdukt () {

    }

    public EinkaufslistProdukt (String insertDate, Produkt produkt, String checkStatus) {
        super(produkt.getBarcode(), produkt.getProductName(), produkt.getProductDescription(),
                produkt.getPackSize(), produkt.getUnit(), produkt.getPackageAmount(), produkt.getLocation());
        this.insertDate = insertDate;
        this.checkStatus = checkStatus;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }
}



