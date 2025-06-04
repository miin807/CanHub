package com.canhub.canhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditarPerfil extends AppCompatActivity implements OnPerfilUpdateListener {

    private TextView editarImagen;
    private LinearLayout editarNombreLayout,editarDescripcion;
    private ImageView imagenPerfil;
    // Se asume que en el layout existe un TextView para mostrar el nombre actual (por ejemplo, como etiqueta)
    private TextView nombrePerfil,descripcionPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_editar_perfil);

        editarImagen = findViewById(R.id.editarImagen);
        editarNombreLayout = findViewById(R.id.editarNombre);
        editarDescripcion = findViewById(R.id.editarDescripcion);
        imagenPerfil = findViewById(R.id.imagenPerfil);
        nombrePerfil = findViewById(R.id.nombrePerfil);
        descripcionPerfil = findViewById(R.id.descrpcionPerfil);


        ImageButton btnAtras = findViewById(R.id.atras);
        btnAtras.setOnClickListener(view -> goPerfil2());

        // Al hacer clic en "editarImagen", se muestra el BottomSheet
        editarImagen.setOnClickListener(v -> {
            PerfilBottomsheet perfilBottomsheet = new PerfilBottomsheet();
            // Gracias a la implementación de OnPerfilUpdateListener, el fragment podrá notificar el cambio
            perfilBottomsheet.show(getSupportFragmentManager(), perfilBottomsheet.getTag());
        });
        imagenPerfil.setOnClickListener(v -> {
            PerfilBottomsheet perfilBottomsheet = new PerfilBottomsheet();
            // Gracias a la implementación de OnPerfilUpdateListener, el fragment podrá notificar el cambio
            perfilBottomsheet.show(getSupportFragmentManager(), perfilBottomsheet.getTag());
        });

        editarNombreLayout.setOnClickListener(v -> mostrarDialogoEditarNombre());
        editarDescripcion.setOnClickListener(v -> mostrarDialogoEditarDescripcion());
        cargarImagenPerfil();
    }

    private void mostrarDialogoEditarNombre() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar nombre");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = input.getText().toString().trim();
            if (!nuevoNombre.isEmpty()) {
                actualizarNombreSupabase(nuevoNombre);
            } else {
                Toast.makeText(EditarPerfil.this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void mostrarDialogoEditarDescripcion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar descripción");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevaDescripcion = input.getText().toString().trim();
            actualizarDescripcionSupabase(nuevaDescripcion);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Actualiza el nombre en la base de datos
    private void actualizarNombreSupabase(String nuevoNombre) {
        SharedPreferences prefs = getSharedPreferences("Sesion", MODE_PRIVATE);
        String userId = prefs.getString("userId", "");
        String accessToken = prefs.getString("accessToken", "");

        if (userId.isEmpty() || accessToken.isEmpty()) {
            Toast.makeText(this, "No se pudo autenticar", Toast.LENGTH_SHORT).show();
            return;
        }

        okhttp3.OkHttpClient client = Supabase.getClient();
        String jsonBody = "{ \"nombre\": \"" + nuevoNombre + "\" }";
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        String url = Supabase.getSupabaseUrl() + "/rest/v1/perfiles?auth_id=eq." + userId;
        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("apikey", Supabase.getSupabaseKey())
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(EditarPerfil.this, "Error de red", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditarPerfil.this, "Nombre actualizado", Toast.LENGTH_SHORT).show();
                        // Guardamos el nuevo nombre en SharedPreferences (opcional)
                        SharedPreferences perfilPrefs = getSharedPreferences("perfil", MODE_PRIVATE);
                        perfilPrefs.edit().putString("nombre", nuevoNombre).apply();
                        // Se envía el extra para actualización inmediata en Perfil2
                        Intent data = new Intent();
                        data.putExtra("nombre_actualizado", nuevoNombre);
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        Toast.makeText(EditarPerfil.this, "Error código " + response.code(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    // Actualiza la descripcion en la base de datos
    private void actualizarDescripcionSupabase(String nuevaDescripcion) {
        SharedPreferences prefs = getSharedPreferences("Sesion", MODE_PRIVATE);
        String userId = prefs.getString("userId", "");
        String accessToken = prefs.getString("accessToken", "");

        if (userId.isEmpty() || accessToken.isEmpty()) {
            Toast.makeText(this, "No se pudo autenticar", Toast.LENGTH_SHORT).show();
            return;
        }

        okhttp3.OkHttpClient client = Supabase.getClient();
        String jsonBody = "{ \"descripcion\": \"" + nuevaDescripcion + "\" }";
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        String url = Supabase.getSupabaseUrl() + "/rest/v1/perfiles?auth_id=eq." + userId;
        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("apikey", Supabase.getSupabaseKey())
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(EditarPerfil.this, "Error de red", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditarPerfil.this, "descripcion actualizada", Toast.LENGTH_SHORT).show();
                        // Guardamos la nueva descripcion en SharedPreferences (opcional)
                        SharedPreferences perfilPrefs = getSharedPreferences("perfil", MODE_PRIVATE);
                        perfilPrefs.edit().putString("descripcion", nuevaDescripcion).apply();
                        // Se envía el extra para actualización inmediata en Perfil2
                        Intent data = new Intent();
                        data.putExtra("descripcion_actualizada", nuevaDescripcion);
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        Toast.makeText(EditarPerfil.this, "Error código " + response.code(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    // Carga la imagen y el nombre desde la base de datos.
    // Se establece directamente <nombre> en el TextView.
    private void cargarImagenPerfil() {
        SharedPreferences prefs = getSharedPreferences("Sesion", MODE_PRIVATE);
        String userId = prefs.getString("userId", "");
        String accessToken = prefs.getString("accessToken", "");

        if (userId.isEmpty() || accessToken.isEmpty()) {
            Toast.makeText(this, "No se pudo autenticar", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Supabase.getSupabaseUrl() + "/rest/v1/perfiles?select=img_user,nombre,descripcion&auth_id=eq." + userId;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", Supabase.getSupabaseKey())
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        Supabase.getClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(EditarPerfil.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() > 0) {
                            JSONObject perfil = jsonArray.getJSONObject(0);
                            String urlImagen = perfil.optString("img_user", "");
                            final String nombre = perfil.optString("nombre", "Usuario");
                            final String descripcion = perfil.optString("descripcion", "Descripcion");
                            runOnUiThread(() -> {
                                // Se establece el texto con la etiqueta fija "Nombre: " seguida del nombre.
                                nombrePerfil.setText(nombre);
                               descripcionPerfil.setText(descripcion);
                                if (!urlImagen.isEmpty()) {
                                    String imageURLConRefresh = urlImagen + "?t=" + System.currentTimeMillis();
                                    Glide.with(EditarPerfil.this)
                                            .load(imageURLConRefresh)
                                            .circleCrop()
                                            .signature(new com.bumptech.glide.signature.ObjectKey(System.currentTimeMillis()))
                                            .skipMemoryCache(true)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .placeholder(R.drawable.perfil_defecto)
                                            .into(imagenPerfil);
                                } else {
                                    imagenPerfil.setImageResource(R.drawable.perfil_defecto);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(EditarPerfil.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void goPerfil2() {
        startActivity(new Intent(this, Perfil2.class));
    }

    @Override
    public void onPerfilUpdated() {
        // Se puede navegar directamente a Perfil2 o recargar la data actualizada
        startActivity(new Intent(this, Perfil2.class));
    }
}