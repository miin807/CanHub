package com.canhub.canhub.formulario;

import android.app.AlertDialog;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.canhub.canhub.Inicio;
import com.canhub.canhub.R;
import com.canhub.canhub.Supabase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


import okhttp3.Call;
import okhttp3.MediaType;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Formulariopt1 extends AppCompatActivity {



    private Button cont;
    private EditText etFecha;
    private String fechaFormateada = "";
    private Button addImgCentro, addImgCansat;
    private EditText centro;
    private ImageView btninfo;

    //Para supabase
    private Uri uriCentro, uriCansat;


    private static final int REQUEST_IMAGE_CENTRO = 1;
    private static final int REQUEST_IMAGE_CANSAT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulariopt1);
        //addimagen

        addImgCentro = findViewById(R.id.imagen_centro);
        addImgCansat = findViewById(R.id.imagen_cansat);
        //calendario
        etFecha = findViewById(R.id.etFecha);

        centro = findViewById(R.id.nombre_centro);
        cont=findViewById(R.id.continuar);

        //cuando se click se abre galeria
        addImgCentro.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_CENTRO);
        });

        addImgCansat.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_CANSAT);
        });


        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona una fecha")
                .build();


        etFecha.setOnClickListener(v -> {
            datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        });

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Formato deseado: yyyy-MM-dd
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaFormateada = formato.format(new Date(selection));

            // Mostrar en el EditText
            etFecha.setText(fechaFormateada);
        });

        //continuar con el formulario
        cont.setOnClickListener(view -> {
            String NombreDelCentro = centro.getText().toString();

            //Evita que suba información sin el nombre o fecha colocada
            if (NombreDelCentro.isEmpty() || getFechaSeleccionada().isEmpty()) {
                showToast("Por favor, ingrese todos los datos.");
                return;
            }
            Intent intent = new Intent(Formulariopt1.this,Formulariopt2.class);

            Toast.makeText(Formulariopt1.this,"Nombre del centro " + NombreDelCentro + "  Fecha: " + getFechaSeleccionada(), Toast.LENGTH_SHORT).show();
            intent.putExtra("nombreCentro", NombreDelCentro);
            intent.putExtra("fechasubida",  getFechaSeleccionada());
            intent.putExtra("fotoCentroUri", uriCentro != null ? uriCentro.toString() : "");
            intent.putExtra("fotoCansatUri", uriCansat != null ? uriCansat.toString() : "");
            startActivity(intent);

        });


        btninfo=findViewById(R.id.icono_informacion);
        btninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog_layout, null);

        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);

        title.setText(R.string.informacion);
        message.setText(R.string.reps_info);

        // Crear y mostrar el diálogo
        AlertDialog dialog = builder.setView(dialogView)
                .setPositiveButton(R.string.ok, (d, which) -> d.dismiss())
                .create();

        dialog.show();

        // Aplicar Poppins al botón "OK" después de mostrar el diálogo
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton != null) {
            Typeface poppins = ResourcesCompat.getFont(this, R.font.poppins); // Asegúrate de tener poppins.ttf en /res/font/
            positiveButton.setTypeface(poppins);
            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.naranja));
        }
    }

    public void goInicio(View view) {
        Intent intent = new Intent(Formulariopt1.this, Inicio.class);
        startActivity(intent);
    }

    public String getFechaSeleccionada() {
        return fechaFormateada;
    }
    // Maneja la imagen seleccionada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedUri = data.getData();

            if (requestCode == REQUEST_IMAGE_CENTRO) {
                uriCentro = selectedUri;
                addImgCentro.setText(R.string.imagen_seleccionada);
                showToast("Imagen seleccionada correctamente");// Muestra una como preview
            } else if (requestCode == REQUEST_IMAGE_CANSAT) {
                uriCansat = selectedUri;
                addImgCansat.setText(R.string.imagen_seleccionada);
                showToast("Imagen seleccionada correctamente");// Muestra una como preview
                // podrías tener otra vista para mostrarla si quieres
            }
        }
    }



    // Muestra mensajes Toast
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

}