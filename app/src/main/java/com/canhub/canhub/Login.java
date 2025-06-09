package com.canhub.canhub;

import android.content.Context;
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

        if (isLoggedIn) {
            inicioSesion = !isGuest; // Solo verdadero si NO es invitado
            goMain();
            return;
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
            if (username.isEmpty() && password.isEmpty()) {
                Toast.makeText(Login.this, "Ingrese el nombre de usuario y la contraseña", Toast.LENGTH_SHORT).show();
            }
            else if (username.isEmpty()) {
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
                    String responseBody = response.body().string(); // Leer la respuesta como String
                    LoginRequest.LoginResponse loginResponse = new Gson().fromJson(responseBody, LoginRequest.LoginResponse.class);

                    if (loginResponse != null && loginResponse.user != null) {
                        String userId = loginResponse.user != null ? loginResponse.user.id : "";
                        String userEmail = (loginResponse.user != null && loginResponse.user.email != null)
                                ? loginResponse.user.email
                                : getEmailFromToken(loginResponse.access_token); // extraer desde el token si es null


//                        runOnUiThread(() -> {
//                            Toast.makeText(Login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
//                            inicioSesion = true;

                            // Guardar sesión en SharedPreferences
                            SharedPreferences preferences = getSharedPreferences("Sesion", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putBoolean("isGuest", false);
                            editor.putString("userId", userId); //  GUARDAMOS EL ID DEL USUARIO
                            editor.putString("userEmail", userEmail);
                            editor.putString("accessToken", loginResponse.access_token);
                            editor.apply();
                            verificarYCrearPerfil(userId, userEmail, loginResponse.access_token);
                            //goMain();

//                        });

                    } else {
                        runOnUiThread(() -> Toast.makeText(Login.this, "Error procesando datos de usuario", Toast.LENGTH_SHORT).show());
                    }


                } else {
                    runOnUiThread(() -> Toast.makeText(Login.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show());
                    inicioSesion = false;
                }
            }
        });

    }
    private void verificarYCrearPerfil(String userId, String email, String accessToken) {
        String url = SUPABASE_URL + "/rest/v1/perfil?user_id=eq." + userId;

        Request request = new Request.Builder()
                .url(url)
                //.addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(Login.this, "Error al verificar perfil", Toast.LENGTH_SHORT).show();
                    goMain();  // Igual avanzamos si falla verificación para no bloquear usuario
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    if (body.equals("[]")) {
                        // No hay perfil, lo creamos
                        crearPerfil(userId, email, accessToken);
                    } else {
                        // Ya existe, redirigimos
                        runOnUiThread(() -> goMain());
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(Login.this, "", Toast.LENGTH_SHORT).show();
                        goMain();  // También avanzamos si error en consulta
                    });
                }
            }
        });
    }

    private void crearPerfil(String userId, String email, String accessToken) {
        String json = "{"
                + "\"user_id\":\"" + userId + "\","
                + "\"email\":\"" + email + "\","
                + "\"nombre\":\"Usuario Nuevo\","
                + "\"descripcion\":\"\""
                + "}";

        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/perfil")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(Login.this, "Error creando perfil", Toast.LENGTH_SHORT).show();
                    goMain();
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(Login.this, "Perfil creado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, "Error al crear perfil", Toast.LENGTH_SHORT).show();
                    }
                    goMain(); // En ambos casos pasamos al inicio
                });
            }
        });
    }

    private String getEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return "";

            String payload = new String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
            org.json.JSONObject jsonObject = new org.json.JSONObject(payload);
            return jsonObject.optString("email", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
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
        private static class LoginResponse {
            String access_token;
            User user;

            static class User {
                String id;
                String email;
            }
        }
    }
    public static boolean getinicioSesion(){
        return inicioSesion;
    }
    //CODIGO PARA VERIFICAR EL USUARIO (AÑADIRLO DENTRO DE FORMULARIO) EL SIGUEINTE CODIGO:
    public static boolean esUsuarioAutenticado(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Sesion", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        boolean isGuest = preferences.getBoolean("isGuest", false);
        return isLoggedIn && !isGuest;
    }
}
