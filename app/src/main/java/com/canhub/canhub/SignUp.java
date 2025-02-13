package com.canhub.canhub;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    EditText usu, email, passwd, confirmpass;
    Button mButton;
    TextView cont;

    FirebaseAuth mAuth;

    private String usuario, mail, password, confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        usu=findViewById(R.id.nombreSignUp);
        email=findViewById(R.id.email);
        passwd=findViewById(R.id.contra);
        confirmpass=findViewById(R.id.confirmar);

        mButton = findViewById(R.id.registrarse);

        mAuth=FirebaseAuth.getInstance();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario=usu.getText().toString().trim();
                mail=email.getText().toString().trim();
                password=passwd.getText().toString().trim();
                confirm=confirmpass.getText().toString().trim();

                if(usuario.isEmpty()) {
                    Toast.makeText(SignUp.this, "Ingrese el nombre de usuario", Toast.LENGTH_SHORT).show();
                }else if(mail.isEmpty()){
                    Toast.makeText(SignUp.this, "Ingrese el email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(SignUp.this, "Ingrese la contraseña", Toast.LENGTH_SHORT).show();
                } else{
                    if(emailValido(mail)){
                        if(password.equals(confirm)){
                            if(confirm.length()>8){
                                mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SignUp.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                            goMain();
                                        }else{
                                            Toast.makeText(SignUp.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(SignUp.this, "Contraseña no valida, mas de 8 carácteres", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(SignUp.this, "Las contraseñas deben ser iguales", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(SignUp.this, "Email no valido", Toast.LENGTH_SHORT).show();
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


    public void goMain (){
        Intent intent2 = new Intent(SignUp.this,Inicio.class);
        startActivity(intent2);
        finish();
    }

    public void returnLogin (View view){
        Intent intent3 = new Intent(SignUp.this,Login.class);
        startActivity(intent3);
    }

    private boolean emailValido(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}