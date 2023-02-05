package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    private String proUnit;
    private double proAmount;
    private String proLocation;

    public static final String PRODUKTNAME_NEW = "produktName_new";
    public static final String PRODUKTBESCHREIBUNG_NEW = "produktBeschreibung_new";
    public static final String NETTOGEWICHT_NEW = "nettogewicht_new";
    public static final String UNIT_NEW = "unit_new";
    public static final String PRODUKTMENGE_NEW = "produktmenge_new";
    public static final String LOCATION_NEW = "location_new";
    public static final String BARCODE_NEW = "barcode_new";

    private String loco;

    Intent intent;
    Produkt produkt;

    public static final String LOCATION_CHANGE = "locationChange";
    public static final String CHANGE_AMOUNT = "amountChange";

    private final String [] array_Units = new String [] {"-", "l", "ml", "g", "kg"};

    private final String [] array_locations = new String [] {"Speis", "Vorratsschrank", "Kühlschrank",
                                                        "Gefrierschrank"};

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

        spLocation.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, array_locations));
        spUnits.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item, array_Units));


        //holt sich Intent wird gesendet von
        // a) Klick auf Eintrag
        // b) Klick auf Haeckchen beim Barcodesanner
        intent = getIntent();

        // Werte von b
        receivingBarcode = intent.getStringExtra(IntoDatabase.BARCODE);
        loco = intent.getStringExtra(IntoDatabase.LOCATION);

        // setzt die Location (wird schon beim Barcodescanner ausgewaehlt)
        spLocation.setSelection(getLocationSelection(loco));

        // wenn intent Extra_Produktname hat, werden alle Parameter uebergeben und Auswahl
        // erscheint im EditText
        // Werte von a
        if(intent.hasExtra(HelperClass.PRODUKTNAME)){
            pName.setText(intent.getStringExtra(HelperClass.PRODUKTNAME));
            pDescription.setText(intent.getStringExtra(HelperClass.PRODUKTBESCHREIBUNG));
            pSize.setText(intent.getStringExtra(HelperClass.NETTOGEWICHT));
            spUnits.setSelection(getUnitSelection(intent.getStringExtra(HelperClass.UNIT)));
            pAmount.setText((intent.getStringExtra(HelperClass.PRODUKTMENGE)));
            spLocation.setSelection(getLocationSelection(intent.getStringExtra(HelperClass.LOCATION)));
        }

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

    public void addToDatabase () {

        HelperClass hp = new HelperClass();
        Intent produkt_data = new Intent();

        if (!checkData() && (!spUnits.getSelectedItem().equals("-"))){

            setResult(RESULT_CANCELED, produkt_data);
            Toast.makeText(getApplicationContext(), R.string.alleFelderAusfuellen_warnung,
                    Toast.LENGTH_SHORT).show();

        } else {

            if (intent.hasExtra(IntoDatabase.BARCODE)) {

                getValues();

                produkt = new Produkt(receivingBarcode, proName, proDescription, proSize, proUnit,
                        proAmount, proLocation);

                hp.putInIntent(produkt, produkt_data);

                setResult(RESULT_OK, produkt_data);
                finish();

            }
            else if (intent.getStringExtra(HelperClass.LOCATION).equals(spLocation.getSelectedItem())
                    && (! intent.getStringExtra(HelperClass.PRODUKTMENGE)
                    .equals(pAmount.getText().toString()))) {

                String barcode = intent.getStringExtra(HelperClass.BARCODE);

                getValues();

                Produkt produktToUpdate = new Produkt(barcode, proName, proDescription, proSize, proUnit,
                        proAmount, proLocation);

                hp.putInIntent(produktToUpdate, produkt_data);
                produkt_data.putExtra(CHANGE_AMOUNT, true);
                setResult(RESULT_OK, produkt_data);
                finish();

            }
            else if ( ! intent.getStringExtra(HelperClass.LOCATION)
                    .equals(spLocation.getSelectedItem().toString())){

                getValues();

                String locationOld = intent.getStringExtra(HelperClass.LOCATION);
                String barcode = intent.getStringExtra(HelperClass.BARCODE);

                double amountOld = Double.parseDouble(intent.getStringExtra(HelperClass.PRODUKTMENGE));
                double amountNew = Double.parseDouble(pAmount.getText().toString());

                if (amountOld < amountNew) {
                    Toast.makeText(getApplicationContext(), "die Anzahl die du verschieben " +
                            "moechtest muss kleiner sein!", Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_CANCELED, produkt_data);
                }
                else {
                    double amountOldLocation = amountOld - amountNew;

                    Produkt produktOldLocation = new Produkt(barcode, proName, proDescription,
                            proSize, proUnit, amountOldLocation, locationOld);
                    hp.putInIntent(produktOldLocation, produkt_data);

                    produkt_data.putExtra(BARCODE_NEW, barcode);
                    produkt_data.putExtra(PRODUKTNAME_NEW, proName);
                    produkt_data.putExtra(PRODUKTBESCHREIBUNG_NEW, proDescription);
                    produkt_data.putExtra(NETTOGEWICHT_NEW, proSize);
                    produkt_data.putExtra(UNIT_NEW, proUnit);
                    produkt_data.putExtra(PRODUKTMENGE_NEW, String.valueOf(proAmount));
                    produkt_data.putExtra(LOCATION_NEW, proLocation);

                    produkt_data.putExtra(LOCATION_CHANGE, true);
                    setResult(RESULT_OK, produkt_data);
                    finish();
                }
            }
        }
    }

    public void getValues () {
        proName = pName.getText().toString();
        proDescription = pDescription.getText().toString();
        proSize = (pSize.getText().toString());
        proUnit = spUnits.getSelectedItem().toString();
        proAmount = Double.parseDouble(pAmount.getText().toString());
        proLocation = spLocation.getSelectedItem().toString();
    }

    public int getUnitSelection (String unit){
        int index;

        switch (unit){
            case "-" :
                index = 0;
                return index;
            case "l" :
                index = 1;
                return index;
            case "ml" :
                index = 2;
                return index;
            case "g" :
                index = 3;
                return index;
            case "kg" :
                index = 4;
                return index;
            default :
                return 0;
        }
    }
}