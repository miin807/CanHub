package com.canhub.canhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class EditarPerfil extends AppCompatActivity {

    private TextView editarImagen;
    private LinearLayout editarNombreLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_editar_perfil);


        editarImagen = findViewById(R.id.editarImagen);
        editarNombreLayout = findViewById(R.id.editarNombre);
        ImageButton btnAtras = findViewById(R.id.atras);
        btnAtras.setOnClickListener(view -> goPerfil2());
        editarImagen.setOnClickListener(v -> {
            PerfilBottomsheet perfilBottomsheet = new PerfilBottomsheet();
            perfilBottomsheet.show(getSupportFragmentManager(), perfilBottomsheet.getTag());
        });


        editarNombreLayout.setOnClickListener(v -> mostrarDialogoEditarNombre());
    }

    private void mostrarDialogoEditarNombre() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar nombre");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = input.getText().toString();

            SharedPreferences prefs = getSharedPreferences("perfil", MODE_PRIVATE);
            prefs.edit().putString("nombre", nuevoNombre).apply();

            setResult(RESULT_OK);
            finish(); // Termina la actividad y regresa a Perfil2
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void goPerfil2(){
        Intent intent = new Intent(this, Perfil2.class);
        startActivity(intent);
    }
}
