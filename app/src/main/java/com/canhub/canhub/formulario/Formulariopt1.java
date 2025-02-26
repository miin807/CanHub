package com.canhub.canhub.formulario;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.canhub.canhub.Inicio;
import com.canhub.canhub.R;
import com.canhub.canhub.Supabase;
import com.google.gson.Gson;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;



import java.util.HashMap;
import java.util.Map;


import javax.security.auth.callback.Callback;

import okhttp3.Call;
import okhttp3.MediaType;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Formulariopt1 extends AppCompatActivity {


    private Button cont;
    private CalendarView calendario;
    private Button addImg;
    private EditText centro;
    private String selectedDate;
    private ImageView previewImageView;

    //Para supabase

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulariopt1);
        //addimagen

        addImg = findViewById(R.id.agregarImagen);
        previewImageView = findViewById(R.id.imagen);
        //calendario
        calendario = findViewById(R.id.calendario);
        centro = findViewById(R.id.nombre_centro);
        cont=findViewById(R.id.continuar);

        //cuando se click se abre galeria
        addImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);


        });

        //sacar dia , mes, year del calendario
        calendario.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // El mes comienza desde 0 (Enero = 0, Febrero = 1, etc.), por eso sumamos 1
            int selectedMonth = month + 1;

            // Mostrar la fecha seleccionada en un Toast
            selectedDate = dayOfMonth + "/" + selectedMonth + "/" + year;
        });
        //continuar con el formulario
        cont.setOnClickListener(view -> {
            Intent intent = new Intent(Formulariopt1.this,Formulariopt2.class);
            String NombreDelCentro = centro.getText().toString();
            Toast.makeText(Formulariopt1.this,"Nombre del centro " + NombreDelCentro + "Fecha: " + selectedDate, Toast.LENGTH_SHORT).show();
            startActivity(intent);
            // 3. INICIAR SUBIDA DE IMAGEN
            uploadImage(NombreDelCentro, selectedDate);
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
            selectedImageUri = data.getData(); // Guardar URI de la imagen
        }
    }


    // Sube la imagen a Supabase Storage
    private void uploadImage(String nombrecentro,String fecha) {
        OkHttpClient client = Supabase.getClient();
        String fileName = nombrecentro.replaceAll("[^a-zA-Z0-9]", "_") + ".jpg"; // Nombre seguro
        String bucketName = "prueba";

        try {
            // 1. CONVERTIR IMAGEN A BYTES
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteStream);
            byte[] imageData = byteStream.toByteArray();

            // 2. CONFIGURAR PETICIÓN HTTP
            Request request = new Request.Builder()
                    .url(Supabase.getSupabaseUrl() + "/storage/v1/object/" + bucketName + "/" + fileName)
                    .header("apikey", Supabase.getSupabaseKey())
                    .header("Authorization", "Bearer " + Supabase.getSupabaseKey())
                    .put(RequestBody.create(imageData, MediaType.parse("image/jpeg")))
                    .build();

            // 3. ENVIAR IMAGEN
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    showToast("Error de red: " + e.getMessage());
                }

                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // 4. OBTENER URL PÚBLICA DE LA IMAGEN
                        String imageUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/public/" + bucketName + "/" + fileName;
                        registerUserInAuth(nombrecentro, fecha, imageUrl);
                    } else {
                        showToast("Error al subir imagen: " + response.code());
                    }
                }
            });
        } catch (IOException e) {
            showToast("Error al leer la imagen: " + e.getMessage());
        }
    }

    // Registra al usuario en Supabase Auth
    private void registerUserInAuth(String nombrecentro, String fecha, String imageUrl) {
        OkHttpClient client = Supabase.getClient();

        // 1. PREPARAR DATOS DE REGISTRO
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombrecentro", nombrecentro);
        payload.put("fecha", fecha);

        // Metadatos adicionales
        Map<String, String> metadata = new HashMap<>();
        metadata.put("nombrecentro", nombrecentro);
        metadata.put("img_perfil", imageUrl);
        payload.put("data", metadata);

        // 2. CONFIGURAR PETICIÓN HTTP
        Request request = new Request.Builder()
                .url(Supabase.getSupabaseUrl() + "/auth/v1/datoscentro")
                .header("apikey", Supabase.getSupabaseKey())
                .post(RequestBody.create(new Gson().toJson(payload), MediaType.get("application/json")))
                .build();

        // 3. ENVIAR REGISTRO
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast("Error de red: " + e.getMessage());
            }

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 4. OBTENER DATOS DE RESPUESTA
                    String responseBody = response.body().string();
                    Map<String, Object> authData = new Gson().fromJson(responseBody, HashMap.class);
                    Map<String, Object> userData = (Map<String, Object>) authData.get("user");

                    String userId = (String) userData.get("id");
                    String accessToken = (String) authData.get("access_token");

                    // 5. GUARDAR DATOS ADICIONALES
                    saveUserData(userId, nombrecentro,fecha, imageUrl, accessToken);
                } else {
                    showToast("Error en registro: " + response.code());
                }
            }
        });
    }


    // Guarda datos en la tabla 'datos'
    private void saveUserData(String userId, String nombrecentro, String fecha, String imageUrl, String accessToken) {
        OkHttpClient client = Supabase.getClient();

        // 1. PREPARAR DATOS
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", userId);
        payload.put("nombrecentro", nombrecentro);
        payload.put("fecha",fecha);
        payload.put("img_perfil", imageUrl);

        // 2. CONFIGURAR PETICIÓN AUTENTICADA
        Request request = new Request.Builder()
                .url(Supabase.getSupabaseUrl() + "/rest/v1/datoscentro")
                .header("apikey", Supabase.getSupabaseKey())
                .header("Authorization", "Bearer " + accessToken) // Token del usuario
                .post(RequestBody.create(new Gson().toJson(payload), MediaType.get("application/json")))
                .build();

        // 3. ENVIAR DATOS
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast("Error de red: " + e.getMessage());
            }
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    showToast("Datos guardados correctamente");
                } else {
                    showToast("Error al guardar: " + response.code());
                }
            }
        });
    }


    // Muestra mensajes Toast
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

}