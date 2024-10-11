package com.moutamid.fallsdelivery.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.fallsdelivery.databinding.ProductBottomsheetBinding;
import com.moutamid.fallsdelivery.models.CartModel;
import com.moutamid.fallsdelivery.models.ProductModel;
import com.moutamid.fallsdelivery.ui.CartActivity;
import com.moutamid.fallsdelivery.ui.OrderActivity;
import com.moutamid.fallsdelivery.utilis.Constants;

import java.util.UUID;

public class BottomSheet extends BottomSheetDialogFragment {
    ProductBottomsheetBinding binding;
    ProductModel model;
    int count = 1;

    public BottomSheet(ProductModel model) {
        this.model = model;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProductBottomsheetBinding.inflate(getLayoutInflater(), container, false);

        binding.price.setText("Price: $" + model.price);
        binding.name.setText(model.name);
        binding.desc.setText(model.description);
        Glide.with(this).load(model.image).into(binding.image);

        binding.minus.setEnabled(false);

        binding.plus.setOnClickListener(v -> {
            ++count;
            binding.count.setText(String.valueOf(count));
            binding.minus.setEnabled(true);
        });

        binding.minus.setOnClickListener(v -> {
            if (count > 1) {
                --count;
                binding.count.setText(String.valueOf(count));
                if (count == 1) binding.minus.setEnabled(false);
            }
        });

        binding.add.setOnClickListener(v -> {
            Constants.showDialog();
            CartModel cartModel = getData();
            Constants.databaseReference().child(Constants.CART).child(Constants.auth().getCurrentUser().getUid())
                    .child(cartModel.uid).setValue(cartModel)
                    .addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnSuccessListener(unused -> {
                        dismiss();
                        Constants.dismissDialog();
                        startActivity(new Intent(requireContext(), CartActivity.class));
                    });
        });

        binding.buy.setOnClickListener(v -> {
            Constants.showDialog();
            CartModel cartModel = getData();
            Constants.databaseReference().child(Constants.CART).child(Constants.auth().getCurrentUser().getUid())
                    .child(cartModel.uid).setValue(cartModel)
                    .addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnSuccessListener(unused -> {
                        dismiss();
                        Constants.dismissDialog();
                        Stash.put(Constants.PASS, cartModel);
                        startActivity(new Intent(requireContext(), OrderActivity.class));
                    });
        });

        return binding.getRoot();
    }

    private CartModel getData() {
        CartModel cartModel = new CartModel();
        cartModel.uid = UUID.randomUUID().toString();
        cartModel.model = this.model;
        cartModel.count = count;
        return cartModel;
    }
}
