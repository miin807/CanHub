package com.canhub.canhub.formulario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.canhub.canhub.R;

public class Formulariopt2 extends AppCompatActivity {
    private EditText descripcion;
    private String Descripcion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulariopt2);

        descripcion = findViewById(R.id.descripcion_texto);

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
    }

    public void goForm1(View view) {
        Intent intent = new Intent(Formulariopt2.this,Formulariopt1.class);
        startActivity(intent);
    }
}