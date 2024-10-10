package com.moutamid.fallsdelivery;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moutamid.fallsdelivery.databinding.ActivityMainBinding;
import com.moutamid.fallsdelivery.fragment.PharmacyFragment;
import com.moutamid.fallsdelivery.fragment.RestaurantFragment;
import com.moutamid.fallsdelivery.utilis.Constants;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);
        Constants.initDialog(this);

        binding.bottomNav.setItemActiveIndicatorColor(ColorStateList.valueOf(getColor(R.color.black2)));
        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.restaurant) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new RestaurantFragment()).commit();
                } else if (item.getItemId() == R.id.pharmacy) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PharmacyFragment()).commit();
                }
                return true;
            }
        });
        binding.bottomNav.setSelectedItemId(R.id.restaurant);

    }
}