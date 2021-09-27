package com.example.myapplication;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Produkt {



    public enum Location {
        pantry,
        pantryboard,
        fridge,
        freezer
    }

    private String barcode;
    private String productName;
    private String productDescription;
    private int packSize;
    private double packageAmount;
    Location location;

    public Produkt (String barcode, String productName, String productDescription,
                   int packSize, double packageAmount, Location location) {
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

    public int getPackSize() {
        return packSize;
    }

    public double getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(double packageAmount) {
        this.packageAmount = packageAmount;
    }

    public Location getLocation() {
        return location;
    }


}

























