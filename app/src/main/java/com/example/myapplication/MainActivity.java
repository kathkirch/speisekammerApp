package com.example.myapplication;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_new;
    private Button bt_fridge;
    private Button bt_freezer;
    private Button bt_pantry;
    private Button bt_pantryBoard;

    Intent intentBarcode;
    Intent intentPantry;
    Intent intentPantryBoard;
    Intent intentFridge;
    Intent intentFreezer;

    private String barcode;
    private String productName;
    private String productDescription;
    private String packSize;
    private String unit;
    private double packageAmount;
    private String location;

    public static final String LOCATION = "location";

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference productNode = database.getReference("products");
    DatabaseReference locationNode;
    DatabaseReference barcodeNode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_new = findViewById(R.id.button_new);
        bt_fridge = findViewById(R.id.button_kuehlschrank);
        bt_freezer = findViewById(R.id.button_tiefkuehler);
        bt_pantry = findViewById(R.id.button_speis);
        bt_pantryBoard = findViewById(R.id.button_vorratsschrank);

        bt_new.setOnClickListener(this::onClick);
        bt_fridge.setOnClickListener(this::onClick);
        bt_freezer.setOnClickListener(this::onClick);
        bt_pantryBoard.setOnClickListener(this::onClick);
        bt_pantry.setOnClickListener(this::onClick);
    }

    /*

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        System.out.println(resultCode + "resultCode");
        System.out.println(InsertProduct.RESULT_OK + " resultcode Insert");

        System.out.println(requestCode + "requestCode");
        System.out.println(IntoDatabase.NEW_UNKNOWEN_PRODUCT_ENTRY + " Request entry");

        if (resultCode == InsertProduct.RESULT_OK &&
                requestCode == IntoDatabase.NEW_UNKNOWEN_PRODUCT_ENTRY){

            Produkt produkt = pushIntentToProdukt(data);

            String locationString = produkt.getLocation();
            String barcodeString = produkt.getBarcode();

            locationNode = productNode.child(locationString);
            barcodeNode = locationNode.child(barcodeString);

            barcodeNode.setValue(produkt);

            Toast.makeText(getApplicationContext(), R.string.produkt_hinzugefuegt,
                    Toast.LENGTH_SHORT).show();

        } else if (resultCode == InsertProduct.RESULT_OK &&
                requestCode == ProductTable.PRODUCT_EDIT_REQUEST_CODE ) {

            System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

            if(data.hasExtra(InsertProduct.CHANGE_AMOUNT)){
                Produkt produktToUpdate = pushIntentToProdukt(data);

                HashMap hashMap = produktToHashMap(produktToUpdate);

                locationNode = productNode.child(produktToUpdate.getLocation());
                barcodeNode = locationNode.child(produktToUpdate.getBarcode());

                barcodeNode.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Produktanzahl wurde aktualisiert",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (data.hasExtra(InsertProduct.LOCATION_CHANGE)){

                Produkt produktToUpdate = pushIntentToProdukt(data);
                HashMap hashMap = produktToHashMap(produktToUpdate);

                DatabaseReference oldLocationNode = productNode.child(produktToUpdate.getLocation());
                barcodeNode = oldLocationNode.child(produktToUpdate.getBarcode());

                barcodeNode.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Produkt wurde verschoben",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Produkt produktNewLocation = pushIntentToProdukt2(data);
                DatabaseReference newLocationNode = productNode.child(produktNewLocation.getLocation());
                barcodeNode = newLocationNode.child(produktNewLocation.getBarcode());
                barcodeNode.setValue(produktNewLocation);
            }
        }
        Toast.makeText(this, "Speichern ergab Probleme", Toast.LENGTH_SHORT).show();
    }

     */

    public Produkt pushIntentToProdukt (Intent data){

        barcode = data.getStringExtra(HelperClass.BARCODE);
        productName = data.getStringExtra(HelperClass.PRODUKTNAME);
        productDescription = data.getStringExtra(HelperClass.PRODUKTBESCHREIBUNG);
        packSize = data.getStringExtra(HelperClass.NETTOGEWICHT);
        unit = data.getStringExtra(HelperClass.UNIT);
        packageAmount = Double.parseDouble(data.getStringExtra(HelperClass.PRODUKTMENGE));
        location = data.getStringExtra(HelperClass.LOCATION);

        Produkt produkt = new Produkt(barcode, productName, productDescription, packSize,
                unit, packageAmount, location);

        return produkt;
    }

    public Produkt pushIntentToProdukt2 (Intent data) {
        barcode = data.getStringExtra(InsertProduct.BARCODE_NEW);
        productName = data.getStringExtra(InsertProduct.PRODUKTNAME_NEW);
        productDescription = data.getStringExtra(InsertProduct.PRODUKTBESCHREIBUNG_NEW);
        packSize = data.getStringExtra(InsertProduct.NETTOGEWICHT_NEW);
        unit = data.getStringExtra(InsertProduct.UNIT_NEW);
        packageAmount = Double.parseDouble(data.getStringExtra(InsertProduct.PRODUKTMENGE_NEW));
        location = data.getStringExtra(InsertProduct.LOCATION_NEW);

        Produkt produkt = new Produkt(barcode, productName, productDescription, packSize,
                unit, packageAmount, location);

        return produkt;

    }

    public HashMap produktToHashMap (Produkt produktToUpdate){

        HashMap hashMap = new HashMap();
        hashMap.put("barcode", produktToUpdate.getBarcode());
        hashMap.put("productName", produktToUpdate.getProductName());
        hashMap.put("productDescription", produktToUpdate.getProductDescription());
        hashMap.put("packSize", produktToUpdate.getPackSize());
        hashMap.put("unit", produktToUpdate.getUnit());
        hashMap.put("packageAmount", produktToUpdate.getPackageAmount());
        hashMap.put("location", produktToUpdate.getLocation());

        return hashMap;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_new:
                intentBarcode = new Intent(this, BarcodeScanner.class);
                startActivity(intentBarcode);
                break;
            case R.id.button_kuehlschrank:
                intentFridge = new Intent(this, ProductTable.class);
                intentFridge.putExtra(LOCATION, "Kühlschrank");
                startActivity(intentFridge);
                break;
            case R.id.button_tiefkuehler:
                intentFreezer = new Intent(this, ProductTable.class);
                intentFreezer.putExtra(LOCATION, "Gefrierschrank");
                startActivity(intentFreezer);
                break;
            case R.id.button_speis:
                intentPantry = new Intent(this, ProductTable.class);
                intentPantry.putExtra(LOCATION, "Speis");
                startActivity(intentPantry);
                break;
            case R.id.button_vorratsschrank:
                intentPantryBoard = new Intent(this, ProductTable.class);
                intentPantryBoard.putExtra(LOCATION, "Vorratsschrank");
                startActivity(intentPantryBoard);
                break;
        }
    }
}