package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class IntoDatabase extends AppCompatActivity {

    public static final String BARCODE = "barcodeText";
    public static final String LOCATION = "location";

    private String receivingBarcode;
    private String loco;

    private boolean barcodeExist;
    private HashMap hashMap;

    boolean productKnowen;

    ActivityResultLauncher<Intent> someActivityResultLauncher;

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference productNode = database.getReference("products");
    DatabaseReference locationNode;
    DatabaseReference barcodeNode;

    final HelperClass hp = new HelperClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        receivingBarcode = intent.getStringExtra(BarcodeScanner.BARCODE);
        loco = intent.getStringExtra(BarcodeScanner.LOCATION);

        locationNode = productNode.child(loco);

        locationNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productKnowen = false;
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Produkt produkt = data.getValue(Produkt.class);
                    String barcodeDB = produkt.getBarcode();

                    if (checkBarcode(receivingBarcode, barcodeDB)) {

                        double newPackageAmount = produkt.getPackageAmount() + 1;
                        produkt.setPackageAmount(newPackageAmount);

                        hashMap = hp.produktToHashMap(produkt);

                        productKnowen = true;
                    }
                }
                operateOnDatabase(productKnowen);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "hier ging etwas schief", Toast.LENGTH_SHORT).show();
            }
        });

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();

                            Produkt produkt = hp.pushIntentToProdukt(data);

                            String locationString = produkt.getLocation();
                            String barcodeString = produkt.getBarcode();

                            DatabaseReference locationNode1 = productNode.child(locationString);
                            DatabaseReference barcodeNode1 = locationNode1.child(barcodeString);

                            barcodeNode1.setValue(produkt).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void aVoid) {
                                    Toast.makeText(getApplicationContext(), R.string.produkt_hinzugefuegt,
                                            Toast.LENGTH_SHORT).show();
                                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(mainIntent);
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.produkt_nicht_gespeichert,
                                    Toast.LENGTH_SHORT).show();
                        }
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

    public void openSomeActivityForResult () {
        Intent insertIntent = new Intent(this, InsertProduct.class);
        insertIntent.putExtra(LOCATION, loco);
        insertIntent.putExtra(BARCODE, receivingBarcode);
        someActivityResultLauncher.launch(insertIntent);
    }

    public void newObjectInDatabase () {
        openSomeActivityForResult();
    }

    public void updatePackageAmount () {
        locationNode = productNode.child(loco);
        barcodeNode = locationNode.child(receivingBarcode);
        barcodeNode.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), R.string.produkt_aktualisiert,
                        Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(IntoDatabase.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }
}