package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

public class ProductTable extends AppCompatActivity {

    private ProductAdapter productAdapter;
    private TextView textView;

    public static final int PRODUCT_EDIT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_table);

        Intent intent = getIntent();
        String location = intent.getStringExtra(MainActivity.LOCATION);

        textView = findViewById(R.id.tvProductTable);
        textView.setText(textView.getText() + location);

        HelperClass hp = new HelperClass();

        View v = this.findViewById(android.R.id.content);
        Context context = getApplicationContext();

        productAdapter = hp.selectProducts(location, v, context);

        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot dataSnapshot, int position) {
                Produkt produkt = dataSnapshot.getValue(Produkt.class);
                Intent intent = new Intent(getApplicationContext(), InsertProduct.class);
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                hp.putInIntent(produkt, intent);
                startActivityForResult(intent, PRODUCT_EDIT_REQUEST_CODE);
                //startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        productAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        productAdapter.stopListening();
    }
}