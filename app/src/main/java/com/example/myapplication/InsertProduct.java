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

    private String receivingBarcode;
    private String proName;
    private String proDescription;
    private int proSize;
    private double proAmount;
    private Produkt.Location proLocation;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        pName = findViewById(R.id.eTpName);
        pDescription = findViewById(R.id.eTpDesc);
        pSize = findViewById(R.id.eTpSize);
        pAmount = findViewById(R.id.eTpAmount);
        spLocation = findViewById(R.id.spLocation);
        btSpeichern = findViewById(R.id.btSave);

        proName = pName.getText().toString();

        spLocation.setAdapter(new ArrayAdapter<Produkt.Location>(this,
                android.R.layout.simple_list_item_1, Produkt.Location.values()));

        Intent intent = getIntent();
        receivingBarcode = intent.getStringExtra(IntoDatabase.BARCODE);

        btSpeichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDatabase();
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

    public void addToDatabase (){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productNode = database.getReference("products");

        Produkt produkt;

        if (checkData() && (receivingBarcode != null)) {
            proName = pName.getText().toString();
            proDescription = pDescription.getText().toString();
            proSize = Integer.parseInt(pSize.getText().toString());
            proAmount = Double.parseDouble(pAmount.getText().toString());
            proLocation = (Produkt.Location) spLocation.getSelectedItem();

            produkt = new Produkt(receivingBarcode, proName, proDescription, proSize,
                    proAmount, proLocation);

            productNode.child(receivingBarcode).setValue(produkt);

        } else {
            Toast.makeText(getApplicationContext(), "Produkt konnte nicht gespeichert werden",
                    Toast.LENGTH_SHORT).show();
        }
    }



}