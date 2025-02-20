package com.canhub.canhub.formulario;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.IOException;
import java.util.Calendar;

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
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            // Convertir URI en Bitmap y mostrarlo en el ImageView
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            previewImageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        addImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });


        calendario.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // El mes comienza desde 0 (Enero = 0, Febrero = 1, etc.), por eso sumamos 1
            int selectedMonth = month + 1;

            // Mostrar la fecha seleccionada en un Toast
            selectedDate = dayOfMonth + "/" + selectedMonth + "/" + year;
        });
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
}