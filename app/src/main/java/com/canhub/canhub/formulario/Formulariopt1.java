package com.canhub.canhub.formulario;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.canhub.canhub.Inicio;
import com.canhub.canhub.R;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Formulariopt1 extends AppCompatActivity {
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc";

    private Button cont;
    private CalendarView calendario;
    private Button addImg;
    private EditText centro;
    private Date selectedDate;
    private ImageView previewImageView;
    private String uri;
    private Uri selectedImageUri;

    private final OkHttpClient client = new OkHttpClient();

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulariopt1);

        // Inicializar elementos de la UI
        addImg = findViewById(R.id.agregarImagen);
        previewImageView = findViewById(R.id.imagen);
        calendario = findViewById(R.id.calendario);
        centro = findViewById(R.id.nombre_centro);
        cont = findViewById(R.id.continuar);

        // Cuando se haga clic en "Agregar Imagen", abrir galería
        addImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        // Obtener fecha del calendario
        calendario.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month + 1, dayOfMonth);
            selectedDate = calendar.getTime();
        });

        // Botón "Continuar" - Guarda en Supabase antes de pasar al siguiente formulario
        cont.setOnClickListener(view -> {
            String NombreDelCentro = centro.getText().toString();

            if (NombreDelCentro.isEmpty() || selectedDate == null) {
                showToast("Por favor, ingrese todos los datos.");
                return;
            }

            if (selectedImageUri != null) {
                subirImagenASupabase(selectedImageUri, NombreDelCentro);
            } else {
                guardarDatosEnSupabase(NombreDelCentro, null);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goInicio(View view) {
        Intent intent = new Intent(Formulariopt1.this, Inicio.class);
        startActivity(intent);
    }

    // Maneja la imagen seleccionada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            uri = selectedImageUri.toString();
            previewImageView.setImageURI(selectedImageUri);
        }
    }

    // Subir imagen a Supabase Storage
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void subirImagenASupabase(Uri uri, String nombreCentro) {
        String fileName = "perfiles/" + System.currentTimeMillis() + ".jpg";
        String storageUrl = SUPABASE_URL + "/storage/v1/object/public/perfiles/" + fileName;

        try {
            byte[] imageBytes = getContentResolver().openInputStream(uri).readAllBytes();
            RequestBody requestBody = RequestBody.create(imageBytes, MediaType.parse("image/jpeg"));

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/storage/v1/object/perfiles/" + fileName)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .put(requestBody)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    showToast("Error al subir imagen");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        guardarDatosEnSupabase(nombreCentro, storageUrl);
                    } else {
                        showToast("Error al guardar imagen");
                    }
                }
            });
        } catch (IOException e) {
            showToast("Error al procesar imagen");
        }
    }

    // Guardar datos en Supabase
    private void guardarDatosEnSupabase(String nombreCentro, String fotoUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombreCentro);
        data.put("fecha", selectedDate.toString());
        data.put("imagen", fotoUrl != null ? fotoUrl : "");

        String jsonBody = new Gson().toJson(data);
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/perfiles")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("Error al guardar datos");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        showToast("Datos guardados correctamente");
                        continuarFormulario(nombreCentro, fotoUrl);
                    });
                } else {
                    showToast("Error al guardar datos en Supabase");
                }
            }
        });
    }

    // Continuar con el formulario después de guardar en Supabase
    private void continuarFormulario(String nombreCentro, String fotoUrl) {
        Intent intent = new Intent(Formulariopt1.this, Formulariopt2.class);
        intent.putExtra("nombreCentro", nombreCentro);
        intent.putExtra("fechasubida", selectedDate.toString());
        intent.putExtra("fotoUri", fotoUrl);
        startActivity(intent);
    }

    // Mostrar mensajes en pantalla
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }
}