package com.canhub.canhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp extends AppCompatActivity {
    private String datos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        Button b = findViewById(R.id.registrarse);
        EditText name = findViewById(R.id.nombreusuario);
        EditText emain = findViewById(R.id.email);
        EditText pasw = findViewById(R.id.password);
        EditText conf = findViewById(R.id.confirpassword);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre,email,contra,confirmarcontra;
                nombre = name.getText().toString();
                email = emain.getText().toString();
                contra = pasw.getText().toString();
                confirmarcontra = conf.getText().toString();
                boolean confirmar = verificarPassword(contra,confirmarcontra);
                if(confirmar == true){
                    Intent intent4=new Intent(SignUp.this, Inicio.class);
                    startActivity(intent4);
                    datos = nombre + "-" + email + "-" + contra + "-" + confirmarcontra ;
                }else{
                    Toast toast = Toast.makeText(SignUp.this, "Verificar contraseña. No coincide la contraseña", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void returnLogin (View view){
        Intent intent3 = new Intent(SignUp.this,Login.class);
        startActivity(intent3);
    }
    public boolean verificarPassword(String contra, String confirmar){
        boolean iguales = true;
        for(int i = 0 ; i < contra.length() ; i ++){
            if(contra.charAt(i) == confirmar.charAt(i)){
                iguales = true;
            }
            else {
                iguales = false;
            }
        }
        return iguales;
    }
}