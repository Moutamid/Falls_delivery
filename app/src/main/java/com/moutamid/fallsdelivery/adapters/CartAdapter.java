package com.moutamid.fallsdelivery.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.fallsdelivery.R;
import com.moutamid.fallsdelivery.models.CartModel;
import com.moutamid.fallsdelivery.ui.OrderActivity;
import com.moutamid.fallsdelivery.utilis.Constants;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ProductVH> {
    Context context;
    ArrayList<CartModel> list;

    public CartAdapter(Context context, ArrayList<CartModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductVH(LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductVH holder, int position) {
        CartModel model = list.get(holder.getAdapterPosition());
        holder.price.setText("Price: $" + model.model.price);
        holder.description.setText(model.model.description);
        holder.name.setText(model.model.name);
        Glide.with(context).load(model.model.image).into(holder.image);

        if (model.count == 1) holder.minus.setEnabled(false);
        holder.count.setText(String.valueOf(model.count));

        holder.plus.setOnClickListener(v -> {
            ++model.count;
            holder.count.setText(String.valueOf(model.count));
            holder.minus.setEnabled(true);

            Constants.databaseReference().child(Constants.CART).child(Constants.auth().getCurrentUser().getUid())
                    .child(model.uid).child("count").setValue(model.count);
        });

        holder.minus.setOnClickListener(v -> {
            if (model.count > 1) {
                --model.count;
                holder.count.setText(String.valueOf(model.count));
                if (model.count == 1) holder.minus.setEnabled(false);

                Constants.databaseReference().child(Constants.CART).child(Constants.auth().getCurrentUser().getUid())
                        .child(model.uid).child("count").setValue(model.count);
            }
        });

         holder.itemView.setOnClickListener(v -> {
             Stash.put(Constants.PASS, model);
             context.startActivity(new Intent(context, OrderActivity.class));
         });

         holder.delete.setOnClickListener(v -> {
             new MaterialAlertDialogBuilder(context)
                     .setCancelable(true)
                     .setTitle("Remove " + model.model.name)
                     .setMessage("Are you sure you want to Remove this item from cart?")
                     .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                     .setPositiveButton("Yes", ((dialog, which) -> {
                         dialog.dismiss();
                         Constants.databaseReference().child(Constants.CART).child(Constants.auth().getCurrentUser().getUid())
                                 .child(model.uid).removeValue()
                                 .addOnSuccessListener(unused -> {
                                     Toast.makeText(context, "Item Removed Successfully", Toast.LENGTH_SHORT).show();
                                 }).addOnFailureListener(e -> {
                                     Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                 });
                     }))
                     .show();
         });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ProductVH extends RecyclerView.ViewHolder {
        TextView price, name, description, count;
        ImageView image;
        MaterialCardView minus, plus;
        Button delete;

        public ProductVH(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
            count = itemView.findViewById(R.id.count);
            image = itemView.findViewById(R.id.image);
            description = itemView.findViewById(R.id.description);
            minus = itemView.findViewById(R.id.minus);
            plus = itemView.findViewById(R.id.plus);
            delete = itemView.findViewById(R.id.delete);
        }
    }

}
