package com.canhub.canhub;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Bottomsheet extends BottomSheetDialogFragment  {
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc";

    private final OkHttpClient client = new OkHttpClient();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bottomsheet, container, false);

        // Find buttons from the layout
        TextView user = view.findViewById(R.id.perfil);
        TextView proyect = view.findViewById(R.id.lanzamiento);
        TextView can = view.findViewById(R.id.canhub2);
        TextView cerrarSesion = view.findViewById(R.id.cerrar_sesion);

        // Set click listener for Algorithm button
        user.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Perfil", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), Perfil2.class);
            startActivity(intent);
            dismiss(); // Close the bottom sheet
        });
        // Set click listener for Course button
        proyect.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Lanzamientos", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), Lanzamiento.class);
            startActivity(intent);
            dismiss(); // Close the bottom sheet
        });

        can.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "CanHub", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), Perfil2.class);
            startActivity(intent);
            dismiss(); // Close the bottom sheet
        });
        cerrarSesion.setOnClickListener(view1 -> cerrarSesionSupabase());
        return view;

    }
    private void cerrarSesionSupabase() {
        SharedPreferences preferences = getActivity().getSharedPreferences("Sesion", MODE_PRIVATE);
        String token = preferences.getString("sessionToken", "");

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/auth/v1/logout")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create("", MediaType.get("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error al cerrar sesión", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                requireActivity().runOnUiThread(() -> {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear(); // Borra todo: sesión e invitado
                    editor.apply();

                    Toast.makeText(getActivity(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    irALogin();
                });
            }
        });

    }
    private void irALogin() {
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }


}