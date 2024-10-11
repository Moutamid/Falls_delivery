package com.moutamid.fallsdelivery.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fxn.stash.Stash;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.moutamid.fallsdelivery.R;
import com.moutamid.fallsdelivery.databinding.ActivityPaymentBinding;
import com.moutamid.fallsdelivery.models.OrderModel;
import com.moutamid.fallsdelivery.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;
    Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);

        binding.toolbar.title.setText("Payment");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.cod.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.proof.setEnabled(!isChecked);
        });

        binding.confirm.setOnClickListener(v -> {
            Constants.showDialog();
            if (!binding.cod.isChecked()) {
                if (image == null) Toast.makeText(this, "Please provide proof of your payment", Toast.LENGTH_SHORT).show();
                else uploadImage();
            } else confirmOrder("");
        });

        binding.proof.setOnClickListener(v -> {
            ImagePicker.with(this).compress(1024).maxResultSize(1080, 1080).start();
        });
    }

    private void uploadImage() {
        Constants.storageReference().child("PROOFS").child(Constants.auth().getCurrentUser().getUid())
                .child(new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(new Date()))
                .putFile(image)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> confirmOrder(uri.toString()));
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmOrder(String link) {
        OrderModel orderModel = (OrderModel) Stash.getObject(Constants.ORDERS, OrderModel.class);
        orderModel.proof = link;
        orderModel.COD = binding.cod.isChecked();

        Constants.databaseReference().child(Constants.ORDERS).child(Constants.auth().getCurrentUser().getUid())
                .child(orderModel.uid).setValue(orderModel)
                .addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(unused -> {
                    Constants.databaseReference().child(Constants.CART).child(Constants.auth().getCurrentUser().getUid())
                            .child(orderModel.cartID).removeValue().addOnSuccessListener(unused1 -> {
                                Toast.makeText(this, "Order is confirmed", Toast.LENGTH_SHORT).show();
                                getOnBackPressedDispatcher().onBackPressed();
                            });
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            image = data.getData();
            String path = getRealPathFromURI(image);
            binding.path.setText(path);
        }
    }

    public String getRealPathFromURI(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(column_index);
            cursor.close();
        }
        return filePath;
    }


}