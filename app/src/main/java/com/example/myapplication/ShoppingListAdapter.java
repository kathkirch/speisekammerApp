package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ShoppingListAdapter extends FirebaseRecyclerAdapter <EinkaufslistProdukt, ShoppingListAdapter.ShoppinglistViewholder >  {


    final HelperClass hp = new HelperClass();
    private OnItemClickListener listener;

    public ShoppingListAdapter (FirebaseRecyclerOptions options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull ShoppinglistViewholder holder, int position, @NonNull EinkaufslistProdukt model) {
        EinkaufslistProdukt eklP = getItem(position);
        String name = eklP.getProductDescription();
        String checkStatus = eklP.getCheckStatus();
        holder.bind(name, checkStatus);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                boolean checkStatus = false;
                if (checkBox.isChecked()){
                    checkStatus = true;
                }
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position, checkStatus);
                }
            }
        });
    }

    @NonNull
    @Override
    public ShoppinglistViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_shoppinglist, parent, false);
        return new ShoppingListAdapter.ShoppinglistViewholder(view);
    }



    class ShoppinglistViewholder extends RecyclerView.ViewHolder {

        private final TextView tvItemName;
        private CheckBox checkBox;

       public ShoppinglistViewholder(@NonNull View itemView) {
           super(itemView);
           tvItemName = itemView.findViewById(R.id.tvShoppingList_produkt);
           checkBox = itemView.findViewById(R.id.checkBox_product);
       }

       public void bind (String name, String checkStatus){
           tvItemName.setText(name);
           if (checkStatus.equals(Checkable.checked)){
               checkBox.setChecked(true);
           } else if (checkStatus.equals(Checkable.unchecked)){
               checkBox.setChecked(false);
           }
       }
   }

   public void setItemCheckStatus (int position, DataSnapshot dataSnapshot, boolean checkStatus) {
       DatabaseReference myRef = dataSnapshot.getRef();
       EinkaufslistProdukt eklp = dataSnapshot.getValue(EinkaufslistProdukt.class);
       if (checkStatus){
           eklp.setCheckStatus(Checkable.checked);
       } else if (!checkStatus){
           eklp.setCheckStatus(Checkable.unchecked);
       }
       HashMap hashMap = hp.itemToHashMap(eklp);
       myRef.updateChildren(hashMap);
   }

   public void deleteCheckedItems (){
       final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("shoppinglist");
       Query checkQuery = myRef.orderByChild("checkStatus").equalTo(Checkable.checked);
       checkQuery.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {

               for (DataSnapshot checkSnap : snapshot.getChildren()){
                   checkSnap.getRef().removeValue();
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               System.out.println(error);

           }
       });

   }

    public interface OnItemClickListener {
        void onItemClick (DataSnapshot dataSnapshot, int postition, boolean checkStatus);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }

}
