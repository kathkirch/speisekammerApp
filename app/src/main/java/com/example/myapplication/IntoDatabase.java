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
    private boolean barcodeExist;
    private HashMap hashMap;

    private static final String TAG = "Info";

    boolean productKnowen;
    DatabaseReference myRef;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_into_database);

        Intent intent = getIntent();
        receivingBarcode = intent.getStringExtra(BarcodeScanner.BARCODE);

        myRef = database.getReference("products");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Produkt produkt = data.getValue(Produkt.class);
                    String barcodeToCheck;
                    barcodeToCheck = produkt.getBarcode();

                    if (checkBarcode(receivingBarcode, barcodeToCheck)) {

                        double newPackageAmount = produkt.getPackageAmount() + 1;
                        produkt.setPackageAmount(newPackageAmount);

                        hashMap = new HashMap();
                        hashMap.put("barcode", produkt.getBarcode());
                        hashMap.put("productName", produkt.getProductName());
                        hashMap.put("productDescription", produkt.getProductDescription());
                        hashMap.put("packSize", produkt.getPackSize());
                        hashMap.put("packageAmount", produkt.getPackageAmount());
                        hashMap.put("location", produkt.getLocation());

                        productKnowen = true;
                        operateOnDatabase(productKnowen);
                    }
                    else {
                        productKnowen = false;
                        operateOnDatabase(productKnowen);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "hier ging etwas schief", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean checkBarcode (String receivingBarcode, String barcodeDB) {
        if (receivingBarcode.equals(barcodeDB)) {
            barcodeExist = true;
        } else {
            barcodeExist = false;
        }
        return barcodeExist;
    }

    public void operateOnDatabase (boolean bool){
        if (bool == true){
            updatePackageAmount();
        }else {
            newObjectinDatabase();
        }
    }

    public void newObjectinDatabase () {
        Intent insertIntent = new Intent(getApplicationContext(), InsertProduct.class);
        insertIntent.putExtra(BARCODE, receivingBarcode);
        startActivity(insertIntent);
    }

    public void updatePackageAmount () {

        myRef.child(receivingBarcode).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Produkt wurde aktualisiert",
                        Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(IntoDatabase.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }
}