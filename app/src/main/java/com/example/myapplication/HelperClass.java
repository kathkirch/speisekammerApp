package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class HelperClass {
    public static final String PRODUKTNAME = "produktName";
    public static final String PRODUKTBESCHREIBUNG = "produktBeschreibung";
    public static final String NETTOGEWICHT = "nettogewicht";
    public static final String UNIT = "unit";
    public static final String PRODUKTMENGE = "produktmenge";
    public static final String LOCATION = "location";
    public static final String BARCODE = "barcode";

    private Query mQuery;
    ProductAdapter productAdapter;
    ShoppingListAdapter shoppingListAdapter;
    ProductAdapter foundProductsAdapter;
    private boolean productFound = true;

    public void putInIntent (Produkt produkt, Intent intent) {
        intent.putExtra(PRODUKTNAME, produkt.getProductName());
        intent.putExtra(PRODUKTBESCHREIBUNG, produkt.getProductDescription());
        intent.putExtra(PRODUKTMENGE, String.valueOf(produkt.getPackageAmount()));
        intent.putExtra(NETTOGEWICHT, produkt.getPackSize());
        intent.putExtra(UNIT, produkt.getUnit());
        intent.putExtra(LOCATION, produkt.getLocation());
        intent.putExtra(BARCODE, produkt.getBarcode());
        System.out.println("put in Intent");
    }

    public String createDate() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
        return currentTime;
    }

    public ShoppingListAdapter selectItems (RecyclerView recyclerView){
        final DatabaseReference listNode = FirebaseDatabase.getInstance().getReference("shoppinglist");
        Query mQuery = listNode.orderByChild("productDescription");

        FirebaseRecyclerOptions<EinkaufslistProdukt> options = new FirebaseRecyclerOptions.Builder<EinkaufslistProdukt>()
                .setQuery(mQuery, EinkaufslistProdukt.class)
                .build();

        shoppingListAdapter = new ShoppingListAdapter(options);
        recyclerView.setAdapter(shoppingListAdapter);

        return shoppingListAdapter;
    }

    public ProductAdapter selectProducts (String location, RecyclerView recyclerView){
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("products");
        DatabaseReference locationNode = myRef.child(location);
        mQuery = locationNode.orderByChild("packageAmount").startAfter(0.0);

        FirebaseRecyclerOptions <Produkt> options = new FirebaseRecyclerOptions.Builder<Produkt>()
                .setQuery(mQuery, Produkt.class)
                .build();

        productAdapter = new ProductAdapter(options);

        recyclerView.setAdapter(productAdapter);

        return productAdapter;
    }

    public ProductAdapter searchForProducts (String searchItem, String location, Context con) {
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("products");
        DatabaseReference locationNode = myRef.child(location);

        mQuery  = locationNode.orderByChild("productDescription").startAt(searchItem).endAt(searchItem+"\uf8ff");

        FirebaseRecyclerOptions <Produkt> opt = new FirebaseRecyclerOptions.Builder<Produkt>()
                .setQuery(mQuery, Produkt.class)
                .build();

        foundProductsAdapter = new ProductAdapter(opt);


        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    Toast.makeText(con, "Produkt gefunden", Toast.LENGTH_LONG).show();
                    productFound = true;

                } else {
                    Toast.makeText(con, "Produkt nicht gefunden", Toast.LENGTH_LONG).show();
                    productFound = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.println(Log.ERROR, "Error", "Databaseerror");

            }
        });

        return foundProductsAdapter;
    }

    public Produkt pushIntentToProdukt (Intent data){
        String barcode = data.getStringExtra(BARCODE);
        String productName = data.getStringExtra(PRODUKTNAME);
        String productDescription = data.getStringExtra(PRODUKTBESCHREIBUNG);
        String packSize = data.getStringExtra(NETTOGEWICHT);
        String unit = data.getStringExtra(UNIT);
        double packageAmount = Double.parseDouble(data.getStringExtra(PRODUKTMENGE));
        String location = data.getStringExtra(LOCATION);

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

    public HashMap itemToHashMap (EinkaufslistProdukt eklp){
        HashMap hashMap = new HashMap();
        hashMap.put("barcode", eklp.getBarcode());
        hashMap.put("checkStatus", eklp.getCheckStatus());
        hashMap.put("insertDate", eklp.getInsertDate());
        hashMap.put("productName", eklp.getProductName());
        hashMap.put("productDescription", eklp.getProductDescription());
        hashMap.put("packSize", eklp.getPackSize());
        hashMap.put("unit", eklp.getUnit());
        hashMap.put("packageAmount", eklp.getPackageAmount());
        hashMap.put("location", eklp.getLocation());

        return hashMap;
    }

    public Produkt pushIntentToProdukt2 (Intent data) {
        String barcode = data.getStringExtra(InsertProduct.BARCODE_NEW);
        String productName = data.getStringExtra(InsertProduct.PRODUKTNAME_NEW);
        String productDescription = data.getStringExtra(InsertProduct.PRODUKTBESCHREIBUNG_NEW);
        String packSize = data.getStringExtra(InsertProduct.NETTOGEWICHT_NEW);
        String unit = data.getStringExtra(InsertProduct.UNIT_NEW);
        double packageAmount = Double.parseDouble(data.getStringExtra(InsertProduct.PRODUKTMENGE_NEW));
        String location = data.getStringExtra(InsertProduct.LOCATION_NEW);

        Produkt produkt = new Produkt(barcode, productName, productDescription, packSize,
                unit, packageAmount, location);

        return produkt;
    }

    public boolean isProductFound() {
        return productFound;
    }

    public void setProductFound(boolean productFound) {
        this.productFound = productFound;
    }
}
