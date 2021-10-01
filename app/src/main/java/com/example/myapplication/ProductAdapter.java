package com.example.myapplication;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProductAdapter extends FirebaseRecyclerAdapter <Produkt, ProductAdapter.ProduktViewholder > {


    private OnItemClickListener listener;


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
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
            tvAmount.setText(amount + " " + tvAmount.getText().toString());
            tvPackSize.setText(tvPackSize.getText().toString() + " " + size);
            tvUnit.setText(unit);
        }
    }


    public interface OnItemClickListener {
        void onItemClick (DataSnapshot dataSnapshot, int postition);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }


}
