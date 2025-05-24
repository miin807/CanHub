package com.canhub.canhub;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class EditarNombre extends Fragment {

    private EditText editNombre;
    private Button btnGuardar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_nombre, container, false);

        editNombre = view.findViewById(R.id.editNombre);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = editNombre.getText().toString().trim();
            if (!nuevoNombre.isEmpty()) {
                actualizarNombreSupabase(nuevoNombre);
            }
        });

        return view;
    }
////    private void actualizarNombreSupabase(String nombre) {
//        // PATCH a Supabase con el nuevo nombre
//        Toast.makeText(getContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
//    }

    private void actualizarNombreSupabase(String nuevoNombre) {
        if (getContext() == null) return;

        // Obtiene el userId y token desde SharedPreferences
        SharedPreferences prefs = getContext().getSharedPreferences("Sesion", getContext().MODE_PRIVATE);
        String userId = prefs.getString("userId", "");
        String accessToken = prefs.getString("accessToken", "");

        if (userId.isEmpty() || accessToken.isEmpty()) {
            Toast.makeText(getContext(), "No se pudo autenticar", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();

        // Construir JSON con el nuevo nombre
        String jsonBody = "{ \"nombre\": \"" + nuevoNombre + "\" }";
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url("https://pzlqlnjkzkxaitkphclx.supabase.co/rest/v1/perfiles?user_id=eq." + userId)
                .patch(body)
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc")
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() != null) {
                    String responseBody = response.body().string();
                    Log.e("SUPABASE_RESPONSE", "Código: " + response.code() + "\nCuerpo: " + responseBody);

                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getContext(), "Error código " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

        });
    }


}
