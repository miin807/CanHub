package com.canhub.canhub.formulario;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.canhub.canhub.Inicio;
import com.canhub.canhub.Perfil;
import com.canhub.canhub.R;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Formulariopt1 extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_PERMISSIONS = 100;

    private Button cont;
    private CalendarView calendario;
    private Button addImg;
    private EditText centro;
    private String selectedDate;
    private ImageView previewImageView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    //Para supabase
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc";
    private static final String BUCKET_NAME = "imagenes";

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

        //seleccionar laimagen de la galeria
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        try {
                            // Convertir URI en Bitmap y mostrarlo en el ImageView
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            previewImageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        //cuando se click se abre galeria
        addImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);

            if (selectedImageUri != null) {
                uploadImageToSupabase(selectedImageUri);
            } else {
                Toast.makeText(Formulariopt1.this, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show();
            }
        });

        //sacar dia , mes, year del calendario
        calendario.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // El mes comienza desde 0 (Enero = 0, Febrero = 1, etc.), por eso sumamos 1
            int selectedMonth = month + 1;

            // Mostrar la fecha seleccionada en un Toast
            selectedDate = dayOfMonth + "/" + selectedMonth + "/" + year;
        });
        //continuar con el formulario
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Formulariopt1.this,Formulariopt2.class);
                String NombreDelCentro = centro.getText().toString();
                Toast.makeText(Formulariopt1.this,"Nombre del centro " + NombreDelCentro + "Fecha: " + selectedDate, Toast.LENGTH_SHORT).show();
                startActivity(intent);

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

    // Método para subir la imagen a Supabase
    private void uploadImageToSupabase(Uri uri) {
        new Thread(() -> {
            try {
                // Convertir imagen a bytes
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                // Generar un nombre único para la imagen
                String fileName = UUID.randomUUID().toString() + ".jpg";

                // Crear la solicitud HTTP
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", fileName,
                                RequestBody.create(MediaType.parse("image/jpeg"), imageBytes))
                        .build();

                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName)
                        .header("Authorization", "Bearer " + SUPABASE_KEY)
                        .header("Content-Type", "image/jpeg")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(Formulariopt1.this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show());
                    Log.d("Supabase", "Imagen subida correctamente");
                } else {
                    Log.e("Supabase", "Error al subir imagen: " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Supabase", "Error al convertir imagen: " + e.getMessage());
            }
        }).start();
    }
}