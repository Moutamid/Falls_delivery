package com.moutamid.fallsdelivery.ui;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.fallsdelivery.R;
import com.moutamid.fallsdelivery.databinding.ActivityOrderDetailBinding;
import com.moutamid.fallsdelivery.models.OrderModel;
import com.moutamid.fallsdelivery.utilis.Constants;

public class OrderDetailActivity extends AppCompatActivity {
    ActivityOrderDetailBinding binding;
    OrderModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("Order Details");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        model = (OrderModel) Stash.getObject(Constants.ORDERS, OrderModel.class);

        binding.username.setText(model.name);
        binding.phone.setText(model.phone);
        binding.address.setText(model.address);

        binding.productName.setText(model.productModel.name);
        binding.quantity.setText("x" + model.quantity);
        binding.price.setText("$" + model.productModel.price);
        binding.total.setText("$" + (model.productModel.price * model.quantity));

        if (model.COD) {
            binding.cod.setText("YES");
            binding.proof.setVisibility(View.GONE);
        } else {
            binding.cod.setText("NO");
            binding.proof.setVisibility(View.VISIBLE);
        }

        binding.complete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setCancelable(true)
                    .setTitle("Order Received")
                    .setMessage("Continue if you received your order")
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Continue", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.databaseReference().child(Constants.ORDERS).child(model.userID).child(model.uid).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Order Completed Successfully", Toast.LENGTH_SHORT).show();
                                    getOnBackPressedDispatcher().onBackPressed();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .show();
        });

        binding.proof.setOnClickListener(v -> {
            showImage();
        });

    }

    private void showImage() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.imageviewer);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(true);
        dialog.show();

        ImageView image = dialog.findViewById(R.id.image);
        View toolbar = dialog.findViewById(R.id.toolbar);
        MaterialCardView back = toolbar.findViewById(R.id.back);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText("Payment Proof");
        back.setOnClickListener(v ->  dialog.dismiss());

        Glide.with(this).load(model.proof).into(image);
    }

}