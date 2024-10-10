package com.moutamid.fallsdelivery.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.fallsdelivery.R;
import com.moutamid.fallsdelivery.models.CategoryModel;
import com.moutamid.fallsdelivery.utilis.CategoryListener;
import com.moutamid.fallsdelivery.utilis.Constants;

import java.util.ArrayList;
import java.util.Collection;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryVH> {
    Context context;
    ArrayList<CategoryModel> list;
    CategoryListener categoryListener;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> list, CategoryListener categoryListener) {
        this.context = context;
        this.list = list;
        this.categoryListener = categoryListener;
    }

    @NonNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryVH(LayoutInflater.from(context).inflate(R.layout.category_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryVH holder, int position) {
        CategoryModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.name);

        holder.itemView.setOnClickListener(v -> categoryListener.onClick(model.name));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CategoryVH extends RecyclerView.ViewHolder {
        TextView name;
        public CategoryVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }

}
