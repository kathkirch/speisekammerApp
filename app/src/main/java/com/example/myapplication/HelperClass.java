package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

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



    public void putInIntent (Produkt produkt, Intent intent) {
        intent.putExtra(PRODUKTNAME, produkt.getProductName());
        intent.putExtra(PRODUKTBESCHREIBUNG, produkt.getProductDescription());
        intent.putExtra(PRODUKTMENGE, String.valueOf(produkt.getPackageAmount()));
        intent.putExtra(NETTOGEWICHT, produkt.getPackSize());
        intent.putExtra(UNIT, produkt.getUnit());
        intent.putExtra(LOCATION, produkt.getLocation());
        intent.putExtra(BARCODE, produkt.getBarcode());
    }

    public ProductAdapter selectProducts (String location, View view, Context context){
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("products");
        DatabaseReference locationNode = myRef.child(location);
        mQuery = locationNode.orderByChild("packageAmount").startAt(1);

        RecyclerView recyclerView = view.findViewById(R.id.productRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        FirebaseRecyclerOptions<Produkt> options = new FirebaseRecyclerOptions.Builder<Produkt>()
                .setQuery(mQuery, Produkt.class)
                .build();

        productAdapter = new ProductAdapter(options);
        recyclerView.setAdapter(productAdapter);

        return productAdapter;
    }
}
