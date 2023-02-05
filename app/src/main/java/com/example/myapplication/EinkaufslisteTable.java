package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class EinkaufslisteTable extends AppCompatActivity {

    private ShoppingListAdapter shoppingListAdapter;

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference listNode = database.getReference("shoppinglist");

    private final HelperClass hp = new HelperClass();
    private final Notifications note = new Notifications();

    final String channelID = "foodi-storage";
    public final static int JOB_ID = 1234;

    MovableFloatingActionButton deleteButton;
    MovableFloatingActionButton addButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einkaufsliste);

        Context context = getApplicationContext();

        RecyclerView recyclerView = findViewById(R.id.shoppingListRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        shoppingListAdapter = hp.selectItems(recyclerView);

        deleteButton = findViewById(R.id.mfbDelete);
        addButton = findViewById(R.id.mfbAdd);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingListAdapter.deleteCheckedItems();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(EinkaufslisteTable.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.popup_edit_text, null);
                mBuilder.setView(dialogView);
                final EditText editText = dialogView.findViewById(R.id.eT_newItemShoppinglist);
                final Button button = dialogView.findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String newItem = editText.getText().toString();
                        addItemInShoppinglist(newItem);
                        editText.setText("");
                    }
                });

                final AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        shoppingListAdapter.setOnItemClickListener(new ShoppingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DataSnapshot dataSnapshot, int postition, boolean checkStatus) {
                shoppingListAdapter.setItemCheckStatus(postition, dataSnapshot, checkStatus);
            }
        });

    }

    public void addItemInShoppinglist (String newItem) {
        String nullstring = " ";
        Produkt nullPro = new Produkt(nullstring, nullstring, newItem, nullstring, nullstring, 0, nullstring);
        EinkaufslistProdukt eklp = new EinkaufslistProdukt(hp.createDate(), nullPro, Checkable.unchecked);
        DatabaseReference dateRef = listNode.child(eklp.getInsertDate());
        dateRef.setValue(eklp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        shoppingListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        shoppingListAdapter.stopListening();
    }
}