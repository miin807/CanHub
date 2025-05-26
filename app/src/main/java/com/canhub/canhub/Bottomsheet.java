package com.canhub.canhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class Bottomsheet extends BottomSheetDialogFragment  {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bottomsheet, container, false);

        // Find buttons from the layout
        TextView user = view.findViewById(R.id.perfil);
        TextView proyect = view.findViewById(R.id.proyecto);
        TextView can = view.findViewById(R.id.canhub2);

        // Set click listener for Algorithm button
        user.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Perfil", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), Perfil2.class);
            startActivity(intent);
            dismiss(); // Close the bottom sheet
        });
        // Set click listener for Course button
        proyect.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Proyecto", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), Perfil2.class);
            startActivity(intent);dismiss(); // Close the bottom sheet
        });

        can.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "CanHub", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), Perfil2.class);
            startActivity(intent);dismiss(); // Close the bottom sheet
        });

        return view;

    }

}