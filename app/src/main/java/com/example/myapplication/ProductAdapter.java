package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class ProductAdapter extends FirebaseRecyclerAdapter <Produkt, ProductAdapter.ProduktViewholder > {


    private OnItemClickListener listener;

    final HelperClass hp = new HelperClass();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    final DatabaseReference shoppingListNode = database.getReference("shoppinglist");

    public ProductAdapter(@NonNull FirebaseRecyclerOptions options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull ProduktViewholder holder, int position, @NonNull Produkt model) {
        Produkt currProduct = getItem(position);
        String name = currProduct.getProductDescription();
        String amount = ( String.valueOf(currProduct.getPackageAmount()));
        String packSize = currProduct.getPackSize();
        String unit = currProduct.getUnit();
        holder.bind(name, amount, packSize, unit);
    }

    @NonNull
    @Override
    public ProduktViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_products, parent, false);

        return new ProductAdapter.ProduktViewholder(view);
    }

    class ProduktViewholder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvPackSize;
        private final TextView tvAmount;
        private final TextView tvUnit;

        public ProduktViewholder (View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvBezeichnung);
            tvPackSize = itemView.findViewById(R.id.tVpSize);
            tvAmount = itemView.findViewById(R.id.tvPackAmount);
            tvUnit = itemView.findViewById(R.id.tvUnit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }


        @SuppressLint("SetTextI18n")
        public void bind (String name, String amount, String size, String unit){
            tvName.setText(name);
            String amountString = amount + " Stück";
            tvAmount.setText(amountString);
            String packString = "a " + size;
            tvPackSize.setText(packString);
            tvUnit.setText(unit);
        }
    }

    public void setItemZero (int position){
        DatabaseReference myRef = getSnapshots().getSnapshot(position).getRef();
        DataSnapshot produktSnap = getSnapshots().getSnapshot(position);
        Produkt myProdukt = produktSnap.getValue(Produkt.class);
        myProdukt.setPackageAmount(0);
        HashMap hashMap = hp.produktToHashMap(myProdukt);
        myRef.updateChildren(hashMap);
    }

    public void productToShoppingList (int position) {
        DataSnapshot produktSnap = getSnapshots().getSnapshot(position);
        Produkt produkt = produktSnap.getValue(Produkt.class);
        EinkaufslistProdukt eklP = new EinkaufslistProdukt(hp.createDate(), produkt, EinkaufslistProdukt.unchecked);
        DatabaseReference dateRef = shoppingListNode.child(eklP.getInsertDate());
        if (produkt.getProductDescription() != null) {
            dateRef.setValue(eklP);
        }
    }


    public interface OnItemClickListener {
        void onItemClick (DataSnapshot dataSnapshot, int postition);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }

}
