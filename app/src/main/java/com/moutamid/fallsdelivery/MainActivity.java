package com.moutamid.fallsdelivery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.moutamid.fallsdelivery.databinding.ActivityMainBinding;
import com.moutamid.fallsdelivery.fragment.PharmacyFragment;
import com.moutamid.fallsdelivery.fragment.RestaurantFragment;
import com.moutamid.fallsdelivery.ui.CartActivity;
import com.moutamid.fallsdelivery.utilis.Constants;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
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
        binding.cart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) &&
                            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION);
                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS);
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION);
                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 111);
            }
        }

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
            Log.d(TAG, "getToken: " + s);
            Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid()).child("fcmToken").setValue(s)
                    .addOnSuccessListener(unused -> Log.d(TAG, "onCreate: value set"))
                    .addOnFailureListener(e -> Log.d(TAG, "error: " + e.getLocalizedMessage()));
        }).addOnFailureListener(e -> {
            Log.d(TAG, "Token Error: " + e.getLocalizedMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }
}