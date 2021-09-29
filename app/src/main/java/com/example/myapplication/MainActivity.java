package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button bt_new;
    Button bt_fridge;
    Button bt_freezer;
    Button bt_pantry;
    Button bt_pantryBoard;

    Intent intentBarcode;
    Intent intentPantry;
    Intent intentPantryBoard;
    Intent intentFridge;
    Intent intentFreezer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_new = findViewById(R.id.button_new);
        bt_fridge = findViewById(R.id.button_kuehlschrank);
        bt_freezer = findViewById(R.id.button_tiefkuehler);
        bt_pantry = findViewById(R.id.button_speis);
        bt_pantryBoard = findViewById(R.id.button_vorratsschrank);

        bt_new.setOnClickListener(this::onClick);
        bt_fridge.setOnClickListener(this::onClick);
        bt_freezer.setOnClickListener(this::onClick);
        bt_pantryBoard.setOnClickListener(this::onClick);
        bt_pantry.setOnClickListener(this::onClick);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_new:
                intentBarcode = new Intent(this, BarcodeScanner.class);
                startActivity(intentBarcode);
                break;
            case R.id.button_kuehlschrank:
                intentFridge = new Intent(this, Fridge.class);
                startActivity(intentFridge);
                break;
            case R.id.button_tiefkuehler:
                intentFreezer = new Intent(this, Freezer.class);
                startActivity(intentFreezer);
                break;
            case R.id.button_speis:
                intentPantry = new Intent(this, Pantry.class);
                startActivity(intentPantry);
                break;
            case R.id.button_vorratsschrank:
                intentPantryBoard = new Intent(this, Pantryboard.class);
                startActivity(intentPantryBoard);
                break;
        }
    }
}