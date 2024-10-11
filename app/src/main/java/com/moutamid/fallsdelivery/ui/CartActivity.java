package com.moutamid.fallsdelivery.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.fallsdelivery.R;
import com.moutamid.fallsdelivery.adapters.CartAdapter;
import com.moutamid.fallsdelivery.databinding.ActivityCartBinding;
import com.moutamid.fallsdelivery.models.CartModel;
import com.moutamid.fallsdelivery.utilis.Constants;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    ArrayList<CartModel> list;
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("Cart");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        list = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.showDialog();
        getData();
    }

    private void getData() {
        Constants.databaseReference().child(Constants.CART).child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Constants.dismissDialog();
                        if (dataSnapshot.exists()) {
                            list.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                CartModel model = snapshot.getValue(CartModel.class);
                                list.add(model);
                            }
                            adapter = new CartAdapter(CartActivity.this, list);
                            binding.itemsRC.setAdapter(adapter);
                        } else {
                            adapter = new CartAdapter(CartActivity.this, new ArrayList<>());
                            binding.itemsRC.setAdapter(adapter);
                            Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Constants.dismissDialog();
                        Toast.makeText(CartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}