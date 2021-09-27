package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class IntoDatabase extends AppCompatActivity {

    public static final String BARCODE = "barcodeText";

    private String receivingBarcode;
    private String barcodeDB;
    private boolean barcodeExist;
    private HashMap hashMap;

    private static final String TAG = "Info";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_into_database);

        Intent intent = getIntent();
        receivingBarcode = intent.getStringExtra(BarcodeScanner.BARCODE);

        intoDatabase();

    }

    public String readBarcodeDB (){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Getting Reference to Root Node
        DatabaseReference myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Produkt produkt = dataSnapshot.getValue(Produkt.class);
                Toast.makeText(getApplicationContext(), "Barcode wird geprueft", Toast.LENGTH_SHORT).show();
                barcodeDB = produkt.getBarcode();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "hier ging etwas schief", Toast.LENGTH_SHORT).show();
            }
        });
        return barcodeDB;
    }

    public boolean checkBarcode (String receivingBarcode) {
        if (receivingBarcode.equals(readBarcodeDB())) {
            barcodeExist = true;
        } else {
            barcodeExist = false;
        }
        return barcodeExist;
    }


    public void intoDatabase () {

        if (checkBarcode(receivingBarcode)){
            updatePackageAmount();
        } else if (!checkBarcode(receivingBarcode)){
            Intent insertIntent = new Intent(getApplicationContext(), InsertProduct.class);
            insertIntent.putExtra(BARCODE, receivingBarcode);
            startActivity(insertIntent);
        }
    }

    public void updatePackageAmount () {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child(receivingBarcode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Produkt produkt = dataSnapshot.getValue(Produkt.class);
                produkt.setPackageAmount(produkt.getPackageAmount()+1);

                //make hasMap
                hashMap = new HashMap();
                hashMap.put("bC", produkt.getBarcode());
                hashMap.put("pN", produkt.getProductName());
                hashMap.put("pD", produkt.getProductDescription());
                hashMap.put("pS", produkt.getPackSize());
                hashMap.put("pA", produkt.getPackageAmount());
                hashMap.put("lo", produkt.getLocation());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Something went wrong! Error:" + databaseError.getMessage() );
            }
        });

        myRef.child(receivingBarcode).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Produkt wurde aktualisiert",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}