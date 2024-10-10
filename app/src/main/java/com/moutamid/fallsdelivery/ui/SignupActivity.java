package com.moutamid.fallsdelivery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fxn.stash.Stash;
import com.moutamid.fallsdelivery.MainActivity;
import com.moutamid.fallsdelivery.R;
import com.moutamid.fallsdelivery.databinding.ActivitySignupBinding;
import com.moutamid.fallsdelivery.models.UserModel;
import com.moutamid.fallsdelivery.utilis.Constants;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("Create Account");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.create.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                Constants.auth().createUserWithEmailAndPassword(
                        binding.email.getEditText().getText().toString(),
                        binding.password.getEditText().getText().toString()
                ).addOnSuccessListener(authResult -> {
                    UserModel userModel = new UserModel(
                            authResult.getUser().getUid(),
                            binding.name.getEditText().getText().toString(),
                            binding.email.getEditText().getText().toString(),
                            binding.password.getEditText().getText().toString());
                    Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid())
                            .setValue(userModel).addOnSuccessListener(unused -> {
                                Constants.dismissDialog();
                                Stash.put(Constants.STASH_USER, userModel);
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            }).addOnFailureListener(e -> {
                                Constants.dismissDialog();
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }

    private boolean valid() {
        if (binding.name.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.email.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.password.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!binding.password.getEditText().getText().toString().equals(binding.rePassword.getEditText().getText().toString())) {
            Toast.makeText(this, "Password didn't match*", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}