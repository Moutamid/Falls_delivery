package com.moutamid.fallsdelivery.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fxn.stash.Stash;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.fallsdelivery.databinding.ActivityOrderBinding;
import com.moutamid.fallsdelivery.models.CartModel;
import com.moutamid.fallsdelivery.models.OrderModel;
import com.moutamid.fallsdelivery.utilis.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class OrderActivity extends AppCompatActivity {
    ActivityOrderBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    CartModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);

        binding.toolbar.title.setText("Complete Order");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        model = (CartModel) Stash.getObject(Constants.PASS, CartModel.class);

        binding.location.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            binding.latitude.getEditText().setText(String.valueOf(currentLatLng.latitude));
                            binding.longitude.getEditText().setText(String.valueOf(currentLatLng.longitude));

                            Geocoder geocoder = new Geocoder(OrderActivity.this, Locale.getDefault());
                            try {
                                // Get address list from latitude and longitude
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                if (addresses != null && !addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    String street = address.getThoroughfare(); // Street name
                                    String streetNumber = address.getSubThoroughfare(); // Street number
                                    String city = address.getLocality(); // City
                                    String postalCode = address.getPostalCode(); // Postal Code
                                    String state = address.getAdminArea(); // State or Administrative Area
                                    String country = address.getCountryName(); // Country

                                    // Format and display the delivery address
                                    String deliveryAddress = (streetNumber != null ? streetNumber + " " : "") +
                                            (street != null ? street + ", " : "") +
                                            (city != null ? city + ", " : "") +
                                            (postalCode != null ? postalCode + ", " : "") +
                                            (state != null ? state + ", " : "") +
                                            (country != null ? country : "");

                                    if (binding.address.getEditText().getText().toString().isEmpty())
                                        binding.address.getEditText().setText(deliveryAddress);
                                } else {
                                    Toast.makeText(OrderActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(OrderActivity.this, "Geocoder service not available", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            new MaterialAlertDialogBuilder(OrderActivity.this)
                                    .setMessage("This function requires a GPS connection")
                                    .setCancelable(false)
                                    .setPositiveButton("open Settings", (dialog, which) -> {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent);
                                    }).setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                                    .show();
                        }
                    });
        });

        binding.order.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                confirmOrder();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private boolean valid() {
        if (binding.name.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.number.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Phone Number is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.address.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.latitude.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Latitude is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.longitude.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Longitude is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void confirmOrder() {
        OrderModel orderModel = getOrder();
        Stash.put(Constants.ORDERS, orderModel);
        Stash.clear(Constants.PASS);
        startActivity(new Intent(this, PaymentActivity.class));
        finish();
    }

    private OrderModel getOrder() {
        OrderModel orderModel = new OrderModel();
        orderModel.uid = UUID.randomUUID().toString();
        orderModel.userID = Constants.auth().getCurrentUser().getUid();
        orderModel.cartID = model.uid;
        orderModel.productModel = model.model;
        orderModel.quantity = model.count;
        orderModel.name = binding.name.getEditText().getText().toString().trim();
        orderModel.phone = binding.number.getEditText().getText().toString().trim();
        orderModel.address = binding.address.getEditText().getText().toString().trim();
        orderModel.latitude = binding.latitude.getEditText().getText().toString().trim();
        orderModel.longitude = binding.longitude.getEditText().getText().toString().trim();
        orderModel.proof = "";
        orderModel.COD = false;
        return orderModel;
    }

}