package com.moutamid.fallsdelivery.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.moutamid.fallsdelivery.R;
import com.moutamid.fallsdelivery.databinding.ActivityWelcomeBinding;
import com.moutamid.fallsdelivery.utilis.Constants;

public class WelcomeActivity extends AppCompatActivity {
    ActivityWelcomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        binding.create.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

        binding.login.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

    }
}