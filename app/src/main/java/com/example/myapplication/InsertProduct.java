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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private double proAmount;
    private String proLocation;

    private String loco;

    private final String [] array_Units = new String [] {"l", "ml", "g", "kg"};

    private final String [] array_locations = new String [] {"Speis", "Vorratsschrank", "Kühlschrank",
                                                        "Gefrierschrank"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        Intent intent = getIntent();

        receivingBarcode = intent.getStringExtra(IntoDatabase.BARCODE);
        loco = intent.getStringExtra(IntoDatabase.LOCATION);

        pName = findViewById(R.id.eTpName);
        pDescription = findViewById(R.id.eTpDesc);
        pSize = findViewById(R.id.eTpSize);
        spUnits = findViewById(R.id.spUnits);
        pAmount = findViewById(R.id.eTpAmount);
        spLocation = findViewById(R.id.spLocation);
        btSpeichern = findViewById(R.id.btSave);

        spLocation.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, array_locations));
        spUnits.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_selectable_list_item, array_Units));


        spLocation.setSelection(getSelection(loco));

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

    public int getSelection (String loco){
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

        proLocation = spLocation.getSelectedItem().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productNode = database.getReference("products");
        DatabaseReference locationNode = productNode.child(proLocation);
        DatabaseReference barcodeNode = locationNode.child(receivingBarcode);
        Produkt produkt;

        if (checkData() && (receivingBarcode != null) && (loco != null)) {
            proName = pName.getText().toString();
            proDescription = pDescription.getText().toString();
            proSize = (pSize.getText().toString() + spUnits.getSelectedItem().toString());
            proAmount = Double.parseDouble(pAmount.getText().toString());


            produkt = new Produkt(receivingBarcode, proName, proDescription, proSize,
                    proAmount, proLocation);


            barcodeNode.setValue(produkt);
            Toast.makeText(getApplicationContext(), R.string.produkt_hinzugefuegt, Toast.LENGTH_SHORT)
                    .show();

        } else {
            Toast.makeText(getApplicationContext(), R.string.produkt_nicht_gespeichert,
                    Toast.LENGTH_SHORT).show();
        }
    }
}