package com.canhub.canhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;



import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    EditText usu, passwd;
    Button mButton;
    TextView cont;
    TextView regs;
    private String username;
    private String password;
    private static boolean inicioSesion;
    private final OkHttpClient client = new OkHttpClient();
    private static final String SUPABASE_URL = "https://pzlqlnjkzkxaitkphclx.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB6bHFsbmpremt4YWl0a3BoY2x4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk0NDM2ODcsImV4cCI6MjA1NTAxOTY4N30.LybznQEqaU6dhIxuFI_SUygPNV_br1IAta099oWQuDc";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        boolean isGuest = preferences.getBoolean("isGuest", false);

        if (isLoggedIn || isGuest) {
            if (isLoggedIn) inicioSesion = true;
            goMain();
            return; // Si ya está logueado o es invitado, lo llevamos a Inicio y evitamos que vea el login
        }

        setContentView(R.layout.activity_login);

        usu = findViewById(R.id.nombre);
        passwd = findViewById(R.id.contrasena);
        mButton = findViewById(R.id.iniciarSesion);
        cont = findViewById(R.id.continuar);
        regs=findViewById(R.id.registro);

        mButton.setOnClickListener(view -> {
            username = usu.getText().toString().trim();
            password = passwd.getText().toString().trim();
            //Toast.makeText(Login.this, "es :" + inicioSesion, Toast.LENGTH_SHORT).show();

            if (username.isEmpty()) {
                Toast.makeText(Login.this, "Ingrese el nombre de usuario", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(Login.this, "Ingrese la contraseña", Toast.LENGTH_SHORT).show();
            } else {
                iniciarSesion(username, password);

            }
        });

        cont.setOnClickListener(view -> {
            inicioSesion=false;
            // Guardar en SharedPreferences que el usuario está entrando como invitado
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putBoolean("isGuest", true);
            editor.apply();

            goMain(); // Redirige a Inicio
        });

        regs.setOnClickListener(view -> goToSignup(view));
    }

    private void iniciarSesion(String email, String password) {
        Gson gson = new Gson();
        String json = gson.toJson(new LoginRequest(email, password));

        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/auth/v1/token?grant_type=password")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(Login.this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(Login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                        inicioSesion = true;

                        // Guardar sesión en SharedPreferences
                        SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        goMain(); // Redirige a Inicio
                    });


                } else {
                    runOnUiThread(() -> Toast.makeText(Login.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show());
                    inicioSesion = false;
                }
            }
        });

    }

    public void goMain() {
        Intent intent = new Intent(Login.this, Inicio.class);
        startActivity(intent);
        finish();
    }
    public void goToSignup(View view)
    {
        Intent intent = new Intent(Login.this, SignUp.class);
        startActivity(intent);
    }
    private static class LoginRequest {
        private final String email;
        private final String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
    public static boolean getinicioSesion(){
        return inicioSesion;
    }
}
