package com.moutamid.fallsdelivery.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.fallsdelivery.models.ProductModel;
import com.moutamid.fallsdelivery.utilis.Constants;
import com.moutamid.fallsdelivery.R;
import com.moutamid.fallsdelivery.models.ProductModel;
import com.moutamid.fallsdelivery.utilis.Constants;
import com.moutamid.fallsdelivery.utilis.ProductListener;

import java.util.ArrayList;
import java.util.Collection;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductVH> implements Filterable {
    Context context;
    ArrayList<ProductModel> list;
    ArrayList<ProductModel> listAll;
    ProductListener productListener;

    public ProductAdapter(Context context, ArrayList<ProductModel> list, ProductListener productListener) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        this.productListener = productListener;
    }

    @NonNull
    @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductVH(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductVH holder, int position) {
        ProductModel model = list.get(holder.getAdapterPosition());
        holder.price.setText("Price: $" + model.price);
        holder.name.setText(model.name);
        Glide.with(context).load(model.image).into(holder.image);

        holder.itemView.setOnClickListener(v -> productListener.onClick(model));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ProductModel> filterList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filterList.addAll(listAll);
            } else {
                for (ProductModel listModel : listAll) {
                    if (listModel.name.toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            listModel.category.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filterList.add(listModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends ProductModel>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ProductVH extends RecyclerView.ViewHolder {
        TextView price, name;
        ImageView image;

        public ProductVH(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }
    }

}
