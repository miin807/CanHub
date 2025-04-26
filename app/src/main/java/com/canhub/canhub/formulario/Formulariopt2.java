package com.canhub.canhub.formulario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import java.util.Date;
import java.util.HashMap;
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
    private Date fechaEnviado;
    private String imagenCentroEnviado;
    private  Uri selectedImageUri, selectedJsonFile;
    private ImageButton subirFich;

    JsonReader jsonReader = new JsonReader(this);



    //Nombres de la url donde se almacena la imagen y tipo de la imagen
    private static final String BUCKET_NAME_1 = "json";
    private static final String BUCKET_NAME = "imagen_Instituto";
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
        fechaEnviado = (Date) getIntent().getSerializableExtra("fechasubida");
        imagenCentroEnviado = getIntent().getStringExtra("fotoUri");
        //pasamos la imagen en string y ahora la volvemos a psar en uri
        selectedImageUri=Uri.parse(imagenCentroEnviado);

        subirFich = findViewById(R.id.imagen_subir);

        subirFich.setOnClickListener(v -> seleccionarArchivo());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void goFormulariopt3(View view) {
        Intent intent = new Intent(Formulariopt2.this,Formulariopt3.class);
        Descripcion = descripcion.getText().toString();

        startActivity(intent);

        // 3. INICIAR SUBIDA DE IMAGEN
        uploadImage(nombreCentroEnviado, fechaEnviado,Descripcion);
    }

    public void goForm1(View view) {
        Intent intent = new Intent(Formulariopt2.this,Formulariopt1.class);
        startActivity(intent);
    }
    // Sube la imagen a Supabase Storage
    private void uploadImage(String nombrecentro, Date fecha, String Descripcion) {
        OkHttpClient client = Supabase.getClient();
        String fileName = nombrecentro.replaceAll("[^a-zA-Z0-9]", "_") + ".jpg";

        // Recuperar el email
        SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        String userEmail = preferences.getString("userEmail", "");


        try (InputStream inputStream = getContentResolver().openInputStream(selectedImageUri)) { // Cierra automáticamente el stream
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                showToast("Error al decodificar la imagen");
                return;
            }

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteStream);
            byte[] imageData = byteStream.toByteArray();
            bitmap.recycle(); // Liberar memoria del bitmap

            // Configurar petición HTTP
            Request request = new Request.Builder()
                    .url(Supabase.getSupabaseUrl() + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName)
                    .header("Authorization", "Bearer " + Supabase.getSupabaseKey())
                    .header("Content-Type", IMAGE_TYPE)
                    .post(RequestBody.create(imageData, MediaType.parse(IMAGE_TYPE)))
                    .build();

            // Enviar imagen
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    showToast("Error de red: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Obtener URL pública usando el método recomendado
                        String imageUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;
                        runOnUiThread(() -> registerUserInAuth(nombrecentro, fecha,Descripcion, imageUrl));
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
    private void registerUserInAuth(String nombrecentro, Date fecha, String Descripcion, String imageUrl) {
        OkHttpClient client = Supabase.getClient();

        // 1. PREPARAR DATOS (SOLO CAMPOS NECESARIOS)
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombrecentro", nombrecentro);
        payload.put("fecha", fecha);
        payload.put("img_centro", imageUrl);
        payload.put("descripcion_centro", Descripcion);

        // 2. CONFIGURAR PETICIÓN HTTP
        Request request = new Request.Builder()
                .url(Supabase.getSupabaseUrl() + "/rest/v1/datoscentro")
                .header("apikey", Supabase.getSupabaseKey())
                .header("Authorization", "Bearer " + Supabase.getSupabaseKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(new Gson().toJson(payload), MediaType.get("application/json")))
                .build();

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
                }
            }
        });
    }
    // Muestra mensajes Toast
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    public void uploadJsonFile(Uri selectedJsonUri, String nombrecentro) {
        String jsonFileName = nombrecentro.replaceAll("[^a-zA-Z0-9]", "_") + ".json";

        // Recuperar el email
//        SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
//        String userEmail = preferences.getString("userEmail", "");

        try (InputStream inputStream = getContentResolver().openInputStream(selectedJsonUri)) {
            if (inputStream == null) {
                showToast("No se pudo abrir el archivo JSON.");
                return;
            }

            // Leer el contenido del archivo JSON
            byte[] jsonData = new byte[inputStream.available()];
            inputStream.read(jsonData);

            // Subir el archivo JSON a Supabase
            Request jsonRequest = new Request.Builder()
                    .url(Supabase.getSupabaseUrl() + "/storage/v1/object/" + BUCKET_NAME_1 + "/" + jsonFileName)
                    .header("Authorization", "Bearer " + Supabase.getSupabaseKey())
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(jsonData, MediaType.parse("application/json")))
                    .build();

            OkHttpClient client = Supabase.getClient();
            client.newCall(jsonRequest).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    showToast("Error de red: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Obtener URL pública del archivo JSON
                        String jsonUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/public/" + BUCKET_NAME_1 + "/" + jsonFileName;
                        runOnUiThread(() -> {
                            // Puedes usar esta URL para registrar los datos en Supabase o como necesites
                            showToast("JSON subido exitosamente. URL: " + jsonUrl);
                        });
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Sin detalles";
                        showToast("Error al subir JSON: " + response.code() + " - " + errorBody);
                    }
                    response.close();
                }
            });

        } catch (IOException e) {
            showToast("Error al leer el archivo JSON: " + e.getMessage());
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