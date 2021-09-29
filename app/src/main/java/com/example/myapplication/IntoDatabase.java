package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class IntoDatabase extends AppCompatActivity {

    public static final String BARCODE = "barcodeText";
    public static final String LOCATION = "location";

    private String receivingBarcode;
    private String loco;
    private boolean barcodeExist;
    private HashMap hashMap;

    private static final String TAG = "Info";

    boolean productKnowen;
    DatabaseReference productNode;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        receivingBarcode = intent.getStringExtra(BarcodeScanner.BARCODE);
        loco = intent.getStringExtra(BarcodeScanner.LOCATION);

        productNode = database.getReference("products");
        //DatabaseReference locationNode = productNode.child();

        productNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                System.out.println("r u ever called? huu?");

                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Produkt produkt = data.getValue(Produkt.class);
                    String barcodeDB = produkt.getBarcode();

                    if (checkBarcode(receivingBarcode, barcodeDB)) {

                        hashMap = new HashMap();
                        double newPackageAmount = produkt.getPackageAmount() + 1;
                        produkt.setPackageAmount(newPackageAmount);

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
       if (receivingBarcode.equals(barcodeDB)){
           barcodeExist = true;
       } else {
           barcodeExist = false;
       }
       return barcodeExist;
    }

    public void operateOnDatabase (boolean bool){
        if (bool == true){
            updatePackageAmount();
        }else if (bool == false){
            newObjectInDatabase();
        }
    }

    public void newObjectInDatabase () {
        Intent insertIntent = new Intent(getApplicationContext(), InsertProduct.class);
        insertIntent.putExtra(LOCATION, loco);
        insertIntent.putExtra(BARCODE, receivingBarcode);
        startActivity(insertIntent);
    }

    public void updatePackageAmount () {
        DatabaseReference locationNode = productNode.child(loco);
        DatabaseReference barcodeNode = locationNode.child(receivingBarcode);
        barcodeNode.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
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