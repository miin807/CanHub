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
    private java.util.Date selectedDate;
    private ImageView previewImageView;
    private String uri;

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
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(year, month + 1 , dayOfMonth);

            selectedDate = calendar.getTime();
        });

        //continuar con el formulario
        cont.setOnClickListener(view -> {
            String NombreDelCentro = centro.getText().toString();

            //Evita que suba información sin el nombre o fecha colocada
            if (NombreDelCentro.isEmpty() || selectedDate == null) {
                showToast("Por favor, ingrese todos los datos.");
                return;
            }
            Intent intent = new Intent(Formulariopt1.this,Formulariopt2.class);

            Toast.makeText(Formulariopt1.this,"Nombre del centro " + NombreDelCentro + "  Fecha: " + selectedDate, Toast.LENGTH_SHORT).show();
            intent.putExtra("nombreCentro", NombreDelCentro);
            intent.putExtra("fechasubida",selectedDate.toString());
            intent.putExtra("fotoUri",uri);
            startActivity(intent);

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
            selectedImageUri = data.getData();// Guardar URI de la imagen
            uri = selectedImageUri.toString();
            previewImageView.setImageURI(selectedImageUri);
        }
    }




    // Muestra mensajes Toast
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

}