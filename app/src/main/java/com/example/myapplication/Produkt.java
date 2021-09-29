package com.example.myapplication;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Produkt implements Comparable <Produkt> {

    private String barcode;
    private String productName;
    private String productDescription;
    private String packSize;
    private double packageAmount;
    private String location;


    public Produkt (){

    }

    public Produkt (String barcode, String productName, String productDescription,
                   String packSize, double packageAmount, String location) {
        this.barcode = barcode;
        this.productName = productName;
        this.productDescription = productDescription;
        this.packSize = packSize;
        this.packageAmount = packageAmount;
        this.location = location;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getPackSize() {
        return packSize;
    }

    public double getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(double packageAmount) {
        this.packageAmount = packageAmount;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int compareTo(Produkt o) {
        int c = this.barcode.compareTo(o.barcode);
        return c;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}

























