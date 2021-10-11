package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ProductTable extends AppCompatActivity {

    private ProductAdapter productAdapter;
    private TextView textView;

    ActivityResultLauncher<Intent> myActivityResultLauncher;

    final HelperClass hp = new HelperClass();

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference productNode = database.getReference("products");
    DatabaseReference locationNode;
    DatabaseReference barcodeNode;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_table);

        Intent intent = getIntent();
        String location = intent.getStringExtra(MainActivity.LOCATION);

        textView = findViewById(R.id.tvProductTable);
        textView.setText(textView.getText() + location);

        HelperClass hp = new HelperClass();

        //View v = this.findViewById(android.R.id.content);
        Context context = getApplicationContext();

        RecyclerView recyclerView = findViewById(R.id.productRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        productAdapter = hp.selectProducts(location, recyclerView);

        // onClickListener damit man auf Eintraege klicken kann um Sie zu aendern
        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot dataSnapshot, int position) {
                Produkt produkt = dataSnapshot.getValue(Produkt.class);
                openActivityForResult(produkt);
            }
        });

        // mit rechts wischen kann Item geloescht werden
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.background))
                        .addSwipeRightActionIcon(R.drawable.delete_image)
                        .addSwipeLeftActionIcon(R.drawable.add_product)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                switch (direction) {
                    case ItemTouchHelper.RIGHT :
                        productAdapter.setItemZero(viewHolder.getAdapterPosition());
                        Toast.makeText(getApplicationContext(), "Produkt aufberaucht",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case ItemTouchHelper.LEFT :
                        productAdapter.productToShoppingList(viewHolder.getAdapterPosition());
                        Toast.makeText(getApplicationContext(),
                                "Produkt der Einkaufsliste hinzugefügt",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }).attachToRecyclerView(recyclerView);


        myActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == InsertProduct.RESULT_OK) {
                    Intent data = result.getData();

                    if (data.hasExtra(InsertProduct.CHANGE_AMOUNT)) {

                        Produkt produktToUpdate = hp.pushIntentToProdukt(data);
                        HashMap hashMap = hp.produktToHashMap(produktToUpdate);
                        locationNode = productNode.child(produktToUpdate.getLocation());
                        barcodeNode = locationNode.child(produktToUpdate.getBarcode());

                        barcodeNode.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Produktanzahl wurde aktualisiert",
                                        Toast.LENGTH_SHORT).show();
                                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(mainIntent);
                            }
                        });
                    } else if (data.hasExtra(InsertProduct.LOCATION_CHANGE)) {

                        Produkt produktToUpdate = hp.pushIntentToProdukt(data);
                        HashMap hashMap = hp.produktToHashMap(produktToUpdate);

                        DatabaseReference oldLocationNode = productNode.child(produktToUpdate.getLocation());
                        barcodeNode = oldLocationNode.child(produktToUpdate.getBarcode());

                        barcodeNode.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Produktanzahl" +
                                                "in " + produktToUpdate.getLocation() + " wurde aktualisiert",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        Produkt produktNewLocation = hp.pushIntentToProdukt2(data);
                        DatabaseReference newLocationNode = productNode.child(produktNewLocation.getLocation());
                        barcodeNode = newLocationNode.child(produktNewLocation.getBarcode());
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    barcodeNode.setValue(produktNewLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Produkt wurde in " +
                                                      produktNewLocation.getLocation() +  " verschoben",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(mainIntent);
                                        }
                                    });
                                } else {
                                    double amountSent = produktNewLocation.getPackageAmount();
                                    Produkt produkt = snapshot.getValue(Produkt.class);
                                    double amountBefore = produkt.getPackageAmount();
                                    double amountAfterUpdate = amountBefore + amountSent;
                                    produkt.setPackageAmount(amountAfterUpdate);

                                    HashMap hashMap = hp.produktToHashMap(produkt);
                                    barcodeNode.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(@NonNull Object o) {
                                            Toast.makeText(getApplicationContext(), "Produkt wurde in " +
                                                            produktNewLocation.getLocation() +  " verschoben",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(mainIntent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "Verbindungsfehler"
                                , Toast.LENGTH_SHORT).show();
                            }
                        };
                        barcodeNode.addListenerForSingleValueEvent(eventListener);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.produkt_nicht_gespeichert,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void openActivityForResult (Produkt produkt) {
        Intent intent = new Intent(getApplicationContext(), InsertProduct.class);
        hp.putInIntent(produkt, intent);
        myActivityResultLauncher.launch(intent);
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