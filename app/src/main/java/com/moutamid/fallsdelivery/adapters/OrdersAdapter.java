package com.moutamid.fallsdelivery.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.fallsdelivery.R;
import com.moutamid.fallsdelivery.models.OrderModel;
import com.moutamid.fallsdelivery.ui.OrderDetailActivity;
import com.moutamid.fallsdelivery.utilis.Constants;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.CategoryVH> {
    Context context;
    ArrayList<OrderModel> list;

    public OrdersAdapter(Context context, ArrayList<OrderModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryVH(LayoutInflater.from(context).inflate(R.layout.order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryVH holder, int position) {
        OrderModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.productModel.name);
        holder.quantity.setText("Quantity: x" + model.quantity);
        holder.price.setText("Price: $" + model.productModel.price);
        holder.subtotal.setText("Sub total: $" + (model.quantity * model.productModel.price));

        Glide.with(context).load(model.productModel.image).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Stash.put(Constants.ORDERS, model);
            context.startActivity(new Intent(context, OrderDetailActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CategoryVH extends RecyclerView.ViewHolder {
        TextView subtotal, quantity, price, name;
        ImageView image;
        public CategoryVH(@NonNull View itemView) {
            super(itemView);
            subtotal = itemView.findViewById(R.id.subtotal);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }
    }

}
