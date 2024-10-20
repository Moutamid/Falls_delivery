package com.moutamid.fallsdelivery.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fxn.stash.Stash;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.fallsdelivery.R;
import com.moutamid.fallsdelivery.SplashActivity;
import com.moutamid.fallsdelivery.databinding.FragmentProfileBinding;
import com.moutamid.fallsdelivery.models.UserModel;
import com.moutamid.fallsdelivery.ui.CurrentOrdersActivity;
import com.moutamid.fallsdelivery.ui.EditProfileActivity;
import com.moutamid.fallsdelivery.utilis.Constants;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);

        binding.editProfile.setOnClickListener(v -> startActivity(new Intent(requireContext(), EditProfileActivity.class)));
        binding.orders.setOnClickListener(v -> startActivity(new Intent(requireContext(), CurrentOrdersActivity.class)));

        binding.logout.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        dialog.dismiss();
                        Constants.auth().signOut();
                        startActivity(new Intent(requireContext(), SplashActivity.class));
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        String[] name = userModel.name.split(" ");

        binding.greeting.setText("Welcome " + name[0] + "!");
    }
}