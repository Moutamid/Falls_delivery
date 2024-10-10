package com.moutamid.fallsdelivery.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.fallsdelivery.R;
import com.moutamid.fallsdelivery.adapters.CategoryAdapter;
import com.moutamid.fallsdelivery.adapters.ProductAdapter;
import com.moutamid.fallsdelivery.databinding.FragmentPharmacyBinding;
import com.moutamid.fallsdelivery.models.CategoryModel;
import com.moutamid.fallsdelivery.models.ProductModel;
import com.moutamid.fallsdelivery.utilis.Constants;

import java.util.ArrayList;
import java.util.Comparator;

public class PharmacyFragment extends Fragment {

    FragmentPharmacyBinding binding;
    ProductAdapter adapter;
    ArrayList<ProductModel> list;


    public PharmacyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPharmacyBinding.inflate(getLayoutInflater(), container, false);


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.showDialog();
        Constants.databaseReference().child(Constants.PHARMACY_CATEGORY)
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        ArrayList<CategoryModel> category = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CategoryModel model = snapshot.getValue(CategoryModel.class);
                            category.add(model);
                        }
                        category.sort(Comparator.comparing(categoryModel -> categoryModel.name));
                        if (!category.isEmpty()) {
                            binding.selectedCategory.setText(category.get(0).name);
                            getData(category.get(0).name);
                        }
                        CategoryAdapter adapter = new CategoryAdapter(requireContext(), category, category1 -> {
                            binding.selectedCategory.setText(category1);
                            getData(category1);
                        });
                        binding.categories.setAdapter(adapter);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void getData(String category1) {
        list = new ArrayList<>();
        Constants.showDialog();
        Constants.databaseReference().child(Constants.PHARMACY).child(category1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Constants.dismissDialog();
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ProductModel topicsModel = dataSnapshot.getValue(ProductModel.class);
                        list.add(topicsModel);
                    }
                    if (!list.isEmpty()){
                        list.sort(Comparator.comparing(categoryModel -> categoryModel.name));
                        binding.productRC.setVisibility(View.VISIBLE);
                        binding.noLayout.setVisibility(View.GONE);
                    } else {
                        binding.productRC.setVisibility(View.GONE);
                        binding.noLayout.setVisibility(View.VISIBLE);
                    }

                    adapter = new ProductAdapter(requireContext(), list);
                    binding.productRC.setAdapter(adapter);
                } else {
                    binding.productRC.setVisibility(View.GONE);
                    binding.noLayout.setVisibility(View.VISIBLE);
                    adapter = new ProductAdapter(requireContext(), new ArrayList<>());
                    binding.productRC.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Constants.dismissDialog();
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}