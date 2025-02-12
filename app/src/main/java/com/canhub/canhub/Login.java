package com.canhub.canhub;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    EditText usu, passwd;
    Button mButton;
    TextView cont;
    private String email;
    private String password;
    // Firebase
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        usu=findViewById(R.id.nombre);
        passwd=findViewById(R.id.contrasena);
        mButton=findViewById(R.id.iniciarSesion);

        firebaseAuth=FirebaseAuth.getInstance();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=usu.getText().toString().trim();
                password=passwd.getText().toString().trim();


                if(email.isEmpty()){
                    usu.setTextColor(Color.RED);
                    usu.setHint("Ingrese el email");
                } else if (password.isEmpty()) {
                    passwd.setTextColor(Color.RED);
                    passwd.setHint("Ingrese la contraseña");
                }else{
                    if(emailValido(email)){
                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    goMain();
                                } else {
                                    Toast.makeText(Login.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        usu.setHint("Email no valido");
                        usu.setTextColor(Color.RED);
                    }
                }
            }
        });

        cont=findViewById(R.id.continuar);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goMain();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    protected void onStart(){
        super.onStart();
        FirebaseUser usuario=firebaseAuth.getCurrentUser();

        if(usuario!=null){
            goMain();
        }
    }

    public void goToSignup (View view){
        Intent intent1 = new Intent(Login.this,SignUp.class);
        startActivity(intent1);

    }

    public void goMain (){
        Intent intent2 = new Intent(Login.this,Inicio.class);
        startActivity(intent2);
        finish();
    }

    private boolean emailValido(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}