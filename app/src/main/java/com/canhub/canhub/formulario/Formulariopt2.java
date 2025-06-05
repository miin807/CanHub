package com.canhub.canhub.formulario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.canhub.canhub.JsonReader;
import com.canhub.canhub.R;
import com.canhub.canhub.Supabase;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Formulariopt2 extends AppCompatActivity {
    private EditText descripcion;
    private String Descripcion;
    private String nombreCentroEnviado;
    private String fechaEnviado;
    private String imagenCentroEnviado, imagenCansatEnviada;
    private  Uri selectecCentroUri, selectedCansatUri;
    private Button subirFich;

    JsonReader jsonReader = new JsonReader(this);



    //Nombres de la url donde se almacena la imagen y tipo de la imagen
    private static final String BUCKET_NAME_1 = "json";
    private static final String BUCKET_NAME = "imagen_Instituto";
    private static final String BUCKET_NAME_2 = "imagencansat";
    private static final String IMAGE_TYPE = "image/jpeg";

    private static final int PICK_JSON_FILE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulariopt2);


        descripcion = findViewById(R.id.descripcion_texto);
        //pasamos los datos desde el otro activity
        nombreCentroEnviado=getIntent().getStringExtra("nombreCentro");
        fechaEnviado = getIntent().getStringExtra("fechasubida");
        imagenCentroEnviado = getIntent().getStringExtra("fotoCentroUri");
        imagenCansatEnviada = getIntent().getStringExtra("fotoCansatUri");
        //pasamos la imagen en string y ahora la volvemos a psar en uri
        selectecCentroUri=Uri.parse(imagenCentroEnviado);
        selectedCansatUri=Uri.parse(imagenCansatEnviada);

        subirFich=findViewById(R.id.btn_select_file);

        subirFich.setOnClickListener(v -> seleccionarArchivo());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void goFormulariopt3(View view) {
        Intent intent = new Intent(Formulariopt2.this, Formulariopt3.class);
        Descripcion = descripcion.getText().toString();
        startActivity(intent);

        // 1. Subir imagen del centro
        String nombreImagenCentro = nombreCentroEnviado.replaceAll("[^a-zA-Z0-9]", "_") + ".jpg";
        uploadImage(selectecCentroUri, BUCKET_NAME, nombreImagenCentro, nombreCentroEnviado, fechaEnviado, Descripcion, true);

        // 2. Subir imagen del cansat (opcional, no se registra en la tabla)
        String nombreImagenCansat = nombreCentroEnviado.replaceAll("[^a-zA-Z0-9]", "_") + "_cansat.jpg";
        uploadImage(selectedCansatUri, BUCKET_NAME_2, nombreImagenCansat, nombreCentroEnviado, fechaEnviado, Descripcion, false);
    }


    public void goForm1(View view) {
        Intent intent = new Intent(Formulariopt2.this,Formulariopt1.class);
        startActivity(intent);
    }
    // Sube la imagen a Supabase Storage
    private void uploadImage(Uri imageUri, String bucketName, String fileName, String nombreCentro, String fecha, String descripcion, boolean isCentro) {
        OkHttpClient client = Supabase.getClient();


        try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                showToast("Error al decodificar la imagen");
                return;
            }

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteStream);
            byte[] imageData = byteStream.toByteArray();
            bitmap.recycle();

            Request request = new Request.Builder()
                    .url(Supabase.getSupabaseUrl() + "/storage/v1/object/" + bucketName + "/" + fileName)
                    .header("Authorization", "Bearer " + Supabase.getSupabaseKey())
                    .header("Content-Type", IMAGE_TYPE)
                    .post(RequestBody.create(imageData, MediaType.parse(IMAGE_TYPE)))
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    showToast("Error al subir imagen: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String imageUrlInst = Supabase.getSupabaseUrl() + "/storage/v1/object/public/" +  BUCKET_NAME + "/" + fileName;
                        String imagenUrlCans = Supabase.getSupabaseUrl() + "/storage/v1/object/public/" + BUCKET_NAME_2 + "/" + fileName;
                        // Si es imagen del centro, registramos en la base de datos
                        if (isCentro) {
                            runOnUiThread(() -> registerUserInAuth(nombreCentro, fecha, descripcion, imageUrlInst, imagenUrlCans));
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Sin detalles";
                        showToast("Error al subir: " + response.code() + " - " + errorBody);
                    }
                    response.close();
                }
            });
        } catch (IOException e) {
            showToast("Error: " + e.getMessage());
        }
    }


    // Registra al usuario en Supabase Auth
    private void registerUserInAuth(String nombrecentro, String fecha, String Descripcion, String imageUrlInstituto, String imagenUrlCansat) {
        OkHttpClient client = Supabase.getClient();

        SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        String accessToken = preferences.getString("accessToken", "");


        // 1. PREPARAR DATOS (SOLO CAMPOS NECESARIOS)
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombrecentro", nombrecentro);
        payload.put("fecha", fecha);
        payload.put("img_centro", imageUrlInstituto);
        payload.put("img_cansat", imagenUrlCansat);

        payload.put("descripcion_centro", Descripcion);

        //List<Map<String, Object>> datalist = new ArrayList<>();
       // datalist.add(payload);

        Log.d("TOKEN", "Access Token: " + accessToken);

        // 2. CONFIGURAR PETICIÓN HTTP
        Request request = new Request.Builder()
                .url(Supabase.getSupabaseUrl() + "/rest/v1/datoscentro")
                .header("apikey", Supabase.getSupabaseKey())
                .header("Authorization", "Bearer " + accessToken)
                .header("Prefer", "resolution=merge-duplicates")
                .header("Content-Type", "application/json")
                .post(RequestBody.create(new Gson().toJson(payload), MediaType.get("application/json")))
                .build();
       // showToast(Supabase.getSupabaseKey());
        // 3. ENVIAR REGISTRO
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast("Error de red: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    showToast("Registro exitoso");
                } else {
                    String errorBody = response.body().string();
                    showToast("Error: " + response.code() + " - " + errorBody);
                   // Log.d("Eror" , response.code() + " - " + errorBody);
                }
            }
        });
    }
    // Muestra mensajes Toast
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    public void uploadJsonFile(Uri selectedJsonUri, String nombrecentro) {
        String jsonFileName = nombrecentro.replaceAll("[^a-zA-Z0-9]", "_") + "_" + fechaEnviado + ".json";

        try (InputStream inputStream = getContentResolver().openInputStream(selectedJsonUri)) {
            if (inputStream == null) {
                showToast("No se pudo abrir el archivo JSON.");
                return;
            }

            // 1. Leer el contenido del archivo JSON
            byte[] jsonData = new byte[inputStream.available()];
            inputStream.read(jsonData);

            // 2. Subir el archivo al Storage
            Request uploadRequest = new Request.Builder()
                    .url(Supabase.getSupabaseUrl() + "/storage/v1/object/" + BUCKET_NAME_1 + "/" + jsonFileName)
                    .header("Authorization", "Bearer " + Supabase.getSupabaseKey())
                    .header("Content-Type", "application/json")
                    .put(RequestBody.create(jsonData, MediaType.parse("application/json")))
                    .build();

            OkHttpClient client = Supabase.getClient();
            client.newCall(uploadRequest).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> showToast("Error al subir archivo: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String publicUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/public/" + BUCKET_NAME_1 + "/" + jsonFileName;

                        SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
                        String userId = preferences.getString("userId", "");
                        String accessToken = preferences.getString("accessToken", "");

                        // ... (código existente para subir el archivo al storage)

                        // Cuando registres en la tabla:
                        Map<String, Object> payload = new HashMap<>();
                        payload.put("file_name", jsonFileName);
                        payload.put("url", publicUrl);
                        payload.put("centro", nombrecentro);
                        payload.put("user_id", userId);

                        Request insertRequest = new Request.Builder()
                                .url(Supabase.getSupabaseUrl() + "/rest/v1/jsonfiles")
                                .header("Authorization", "Bearer " + accessToken)  // Usa el token real
                                .header("apikey", Supabase.getSupabaseKey())
                                .header("Content-Type", "application/json")
                                .post(RequestBody.create(new Gson().toJson(payload), MediaType.get("application/json")))
                                .build();

                        client.newCall(insertRequest).enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                runOnUiThread(() -> showToast("Error al registrar: " + e.getMessage()));
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                String responseBody = response.body() != null ? response.body().string() : "{}";
                                if (response.isSuccessful()) {
                                    runOnUiThread(() -> showToast("JSON registrado exitosamente"));
                                } else {
                                    runOnUiThread(() -> {
                                        showToast("Error al registrar JSON: " + response.code());
                                        Log.e("JSON_UPLOAD", "Error: " + response.code() + " - " + responseBody);
                                    });
                                }
                                response.close();
                            }
                        });
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Sin detalles";
                        runOnUiThread(() -> {
                            showToast("Error al subir: " + response.code());
                            Log.e("STORAGE_UPLOAD", "Error: " + response.code() + " - " + errorBody);
                        });
                    }
                    response.close();
                }
            });
        } catch (IOException e) {
            showToast("Error al leer JSON: " + e.getMessage());
        }
    }


    // Abre el activity para seleccionar el json
    private void seleccionarArchivo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/json"); // Solo archivos JSON
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_JSON_FILE);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_JSON_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                // Llamamos a la clase externa para leer el JSON
                uploadJsonFile(uri, nombreCentroEnviado);
            }
        }
    }
}