package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

public class ProductTable extends AppCompatActivity {

    private ProductAdapter productAdapter;
    private TextView textView;


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
                hp.putInIntent(produkt, intent);
                startActivity(intent);
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