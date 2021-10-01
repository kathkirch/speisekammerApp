package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class InsertProduct extends AppCompatActivity {

    private EditText pName;
    private EditText pDescription;
    private EditText pSize;
    private EditText pAmount;
    private Button btSpeichern;
    private Spinner spLocation;
    private Spinner spUnits;

    private String receivingBarcode;
    private String proName;
    private String proDescription;
    private String proSize;
    private String proUnit;
    private double proAmount;
    private String proLocation;

    private String loco;

    Intent intent;
    Produkt produkt;


    private final String [] array_Units = new String [] {"l", "ml", "g", "kg"};

    private final String [] array_locations = new String [] {"Speis", "Vorratsschrank", "Kühlschrank",
                                                        "Gefrierschrank"};

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference productNode = database.getReference("products");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        pName = findViewById(R.id.eTpName);
        pDescription = findViewById(R.id.eTpDesc);
        pSize = findViewById(R.id.eTpSize);
        spUnits = findViewById(R.id.spUnits);
        pAmount = findViewById(R.id.eTpAmount);
        spLocation = findViewById(R.id.spLocation);
        btSpeichern = findViewById(R.id.btSave);

        intent = getIntent();

        receivingBarcode = intent.getStringExtra(IntoDatabase.BARCODE);
        loco = intent.getStringExtra(IntoDatabase.LOCATION);

        spLocation.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, array_locations));
        spUnits.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_selectable_list_item, array_Units));

        // wenn intent Extra Produktname hat, werden alle Parameter uebergeben und Auswahl
        // erscheint im EditText
        if(intent.hasExtra(HelperClass.PRODUKTNAME)){
            pName.setText(intent.getStringExtra(HelperClass.PRODUKTNAME));
            pDescription.setText(intent.getStringExtra(HelperClass.PRODUKTBESCHREIBUNG));
            pSize.setText(intent.getStringExtra(HelperClass.NETTOGEWICHT));
            spUnits.setSelection(getUnitSelection(intent.getStringExtra(HelperClass.UNIT)));
            pAmount.setText((intent.getStringExtra(HelperClass.PRODUKTMENGE)));
            spLocation.setSelection(getLocationSelection(intent.getStringExtra(HelperClass.LOCATION)));
        }

        // setzt die Location (wird schon beim Barcodescanner ausgewaehlt)
        spLocation.setSelection(getLocationSelection(loco));

        btSpeichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDatabase();
                Intent mainIntent = new Intent(InsertProduct.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }

    boolean isEmpty (EditText et){
        CharSequence string = et.getText().toString();
        return TextUtils.isEmpty(string);
    }

    public boolean checkData(){
        boolean dataChecked = true;
        if ( (isEmpty(pName)) || (isEmpty(pDescription)) || (isEmpty(pSize)) || (isEmpty(pAmount))){
            Toast.makeText(getApplicationContext(), R.string.alleFelderAusfuellen_warnung,
                    Toast.LENGTH_SHORT).show();
            dataChecked = false;
        }
        return dataChecked;
    }

    public int getLocationSelection (String loco){
        int index;
        switch (loco){
            case "Speis" :
                index = 0;
                return index;
            case "Vorratsschrank" :
                index = 1;
                return index;
            case "Kühlschrank" :
                index = 2;
                return index;
            case "Gefrierschrank" :
                index = 3;
                return index;
            default:
                return 0;
        }
    }

    public void addToDatabase (){

        DatabaseReference locationNode;
        DatabaseReference barcodeNode;

        if (checkData()){

            proName = pName.getText().toString();
            proDescription = pDescription.getText().toString();
            proSize = (pSize.getText().toString());
            proUnit = spUnits.getSelectedItem().toString();
            proAmount = Double.parseDouble(pAmount.getText().toString());
            proLocation = spLocation.getSelectedItem().toString();

            if ((receivingBarcode != null) && (loco != null)) {

                locationNode = productNode.child(proLocation);
                barcodeNode = locationNode.child(receivingBarcode);

                produkt = new Produkt(receivingBarcode, proName, proDescription, proSize, proUnit,
                        proAmount, proLocation);

                barcodeNode.setValue(produkt);
                Toast.makeText(getApplicationContext(), R.string.produkt_hinzugefuegt, Toast.LENGTH_SHORT)
                        .show();
            }
            else if (proLocation.equals(spLocation.getSelectedItem())
                    && (proAmount != Double.parseDouble(pAmount.getText().toString()))) {

                String barcode = intent.getStringExtra(HelperClass.BARCODE);

                locationNode = productNode.child(proLocation);
                barcodeNode = locationNode.child(barcode);

                proAmount = Double.parseDouble(pAmount.getText().toString());

                HashMap hashMap = new HashMap();
                hashMap.put("barcode", barcode);
                hashMap.put("productName", proName);
                hashMap.put("productDescription", proDescription);
                hashMap.put("packSize", proSize);
                hashMap.put("unit", proUnit);
                hashMap.put("packageAmount", proAmount);
                hashMap.put("location", proLocation);

                barcodeNode.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Produktanzahl wurde aktualisiert",
                                Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(InsertProduct.this, MainActivity.class);
                        startActivity(mainIntent);
                    }
                });
            }
            else if ((intent.getStringExtra(HelperClass.BARCODE) != null)
                    && ( intent.getStringExtra(HelperClass.LOCATION).equals(spLocation.getSelectedItem()) == false)){

                String locationNew = spLocation.getSelectedItem().toString();
                String locationOld = intent.getStringExtra(HelperClass.LOCATION);

                double amountOldLocation = 0;

                double amountOld = Double.parseDouble(intent.getStringExtra(HelperClass.PRODUKTMENGE));
                double amountNew = Double.parseDouble(pAmount.getText().toString());

                // hier auch noch ganze methode in if else reintun sonst bringt das nix
                if (amountNew <= amountOld) {
                    amountOldLocation = amountOld - amountNew;
                }

                String barcode = intent.getStringExtra(HelperClass.BARCODE);
                proName = pName.getText().toString();
                proDescription = pDescription.getText().toString();
                proSize = pSize.getText().toString();
                proUnit = spUnits.getSelectedItem().toString();

                HashMap hashMap = new HashMap();
                hashMap.put("barcode", barcode);
                hashMap.put("productName", proName);
                hashMap.put("productDescription", proDescription);
                hashMap.put("packSize", proSize);
                hashMap.put("unit", proUnit);
                hashMap.put("packageAmount", amountOldLocation);
                hashMap.put("location", locationOld);

                produkt = new Produkt(barcode, proName, proDescription, proSize, proUnit, amountNew, locationNew);

                DatabaseReference newLocationNode = productNode.child(locationNew);
                barcodeNode = newLocationNode.child(barcode);
                barcodeNode.setValue(produkt);

                DatabaseReference oldLocationNode = productNode.child(locationOld);
                barcodeNode = oldLocationNode.child(barcode);

                barcodeNode.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Produkt wurde verschoben",
                                Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(InsertProduct.this, MainActivity.class);
                        startActivity(mainIntent);
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(), R.string.produkt_nicht_gespeichert,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Kathi schreib Methode die zurueckgibt ob Location geaendert wurde oder nicht
    // Rueckgabewert boolean true or false
    // du brauchst das fuer die if else dinger!

    public int getUnitSelection (String unit){
        int index;

        switch (unit){
            case "l" :
                index = 0;
                return index;
            case "ml" :
                index = 1;
                return index;
            case "g" :
                index = 2;
                return index;
            case "kg" :
                index = 3;
                return index;
            default :
                return 0;
        }
    }
}