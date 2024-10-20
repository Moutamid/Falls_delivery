package com.moutamid.fallsdelivery.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.moutamid.fallsdelivery.databinding.ActivityEditProfileBinding;
import com.moutamid.fallsdelivery.models.UserModel;
import com.moutamid.fallsdelivery.utilis.Constants;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("Update Profile");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.email.setEnabled(false);

        Constants.initDialog(this);

        UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);

        binding.name.getEditText().setText(userModel.name);
        binding.email.getEditText().setText(userModel.email);

        binding.update.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid())
                        .child("name").setValue(binding.name.getEditText().getText().toString().trim())
                        .addOnSuccessListener(unused -> {
                            Constants.dismissDialog();
                            userModel.name = binding.name.getEditText().getText().toString().trim();
                            Stash.put(Constants.STASH_USER, userModel);
                            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            getOnBackPressedDispatcher().onBackPressed();
                        }).addOnFailureListener(e -> {
                            Constants.dismissDialog();
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }

    private boolean valid() {
        if (binding.name.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}