package com.canhub.canhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {
    EditText usu, email, passwd, confirmpass;
    Button mButton;
    TextView cont;

    private String usuario, mail, password, confirm;
    private final OkHttpClient client = new OkHttpClient();
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        usu = findViewById(R.id.nombreSignUp);
        email = findViewById(R.id.email);
        passwd = findViewById(R.id.contra);
        confirmpass = findViewById(R.id.confirmar);
        mButton = findViewById(R.id.registrarse);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario = usu.getText().toString().trim();
                mail = email.getText().toString().trim();
                password = passwd.getText().toString().trim();
                confirm = confirmpass.getText().toString().trim();

                if (usuario.isEmpty()) {
                    Toast.makeText(SignUp.this, "Ingrese el nombre de usuario", Toast.LENGTH_SHORT).show();
                } else if (mail.isEmpty()) {
                    Toast.makeText(SignUp.this, "Ingrese el email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(SignUp.this, "Ingrese la contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    if (emailValido(mail)) {
                        if (password.equals(confirm)) {
                            if (confirm.length() >= 8) {
                                registrarUsuario(mail, password);
                            } else {
                                Toast.makeText(SignUp.this, "Contraseña no válida, debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignUp.this, "Las contraseñas deben ser iguales", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUp.this, "Email no válido", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cont = findViewById(R.id.continuar);
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

    private void registrarUsuario(String email, String password) {
        Gson gson = new Gson();
        String json = gson.toJson(new UserRequest(email, password));

        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/auth/v1/signup")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(SignUp.this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(SignUp.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        goMain();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(SignUp.this, "Error al registrar usuario: " + response.code() + "\n" + responseBody, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    public void goMain() {
        Intent intent2 = new Intent(SignUp.this, Inicio.class);
        startActivity(intent2);
        finish();
    }

    public void returnLogin(View view) {
        Intent intent3 = new Intent(SignUp.this, Login.class);
        startActivity(intent3);
    }

    private boolean emailValido(String email) {
        String expression = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(expression);
    }

    private static class UserRequest {
        private final String email;
        private final String password;

        public UserRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
