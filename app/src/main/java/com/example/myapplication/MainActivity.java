package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_new;
    private Button bt_fridge;
    private Button bt_freezer;
    private Button bt_pantry;
    private Button bt_pantryBoard;
    private Button bt_shoppinglist;

    Intent intentBarcode;
    Intent intentPantry;
    Intent intentPantryBoard;
    Intent intentFridge;
    Intent intentFreezer;
    Intent intentshoppingList;

    public static final String LOCATION = "location";

    Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_new = findViewById(R.id.button_new);
        bt_fridge = findViewById(R.id.button_kuehlschrank);
        bt_freezer = findViewById(R.id.button_tiefkuehler);
        bt_pantry = findViewById(R.id.button_speis);
        bt_pantryBoard = findViewById(R.id.button_vorratsschrank);
        bt_shoppinglist = findViewById(R.id.button_einkaufsliste);

        bt_new.setOnClickListener(this::onClick);
        bt_fridge.setOnClickListener(this::onClick);
        bt_freezer.setOnClickListener(this::onClick);
        bt_pantryBoard.setOnClickListener(this::onClick);
        bt_pantry.setOnClickListener(this::onClick);
        bt_shoppinglist.setOnClickListener(this::onClick);

        /*
        serviceIntent = new Intent(this, Notifications.class);
        startService(serviceIntent);

         */
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_new:
                intentBarcode = new Intent(this, BarcodeScanner.class);
                startActivity(intentBarcode);
                break;
            case R.id.button_kuehlschrank:
                intentFridge = new Intent(this, ProductTable.class);
                intentFridge.putExtra(LOCATION, "Kühlschrank");
                startActivity(intentFridge);
                break;
            case R.id.button_tiefkuehler:
                intentFreezer = new Intent(this, ProductTable.class);
                intentFreezer.putExtra(LOCATION, "Gefrierschrank");
                startActivity(intentFreezer);
                break;
            case R.id.button_speis:
                intentPantry = new Intent(this, ProductTable.class);
                intentPantry.putExtra(LOCATION, "Speis");
                startActivity(intentPantry);
                break;
            case R.id.button_vorratsschrank:
                intentPantryBoard = new Intent(this, ProductTable.class);
                intentPantryBoard.putExtra(LOCATION, "Vorratsschrank");
                startActivity(intentPantryBoard);
                break;
            case R.id.button_einkaufsliste:
                intentshoppingList = new Intent(this, EinkaufslisteTable.class);
                startActivity(intentshoppingList);
        }
    }
}