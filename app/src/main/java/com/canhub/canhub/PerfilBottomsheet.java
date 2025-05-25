package com.canhub.canhub;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class PerfilBottomsheet extends BottomSheetDialogFragment {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final String IMAGE_TYPE = "image/jpeg";
    private Uri selectedImageUri;
    private static final String BUCKET_NAME = "imagenusuario";
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // Guardamos el contexto de la aplicación para usarlo en operaciones laterales.
    private Context appContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Guardamos el contexto de aplicación. Es seguro usarlo mucho después de que el fragmento se "desprenda"
        appContext = context.getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_perfil_bottomsheet, container, false);

        // Inicializa el launcher para obtener el resultado de selección de imagen.
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("PerfilBottomsheet", "Activity result recibido: " + result.getResultCode());
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        Log.d("PerfilBottomsheet", "Imagen seleccionada: " + imageUri);
                        if (imageUri != null) {
                            subirImagenASupabase(imageUri);
                            dismiss();
                        }
                    }
                });

        LinearLayout seleccionarImagenLayout = view.findViewById(R.id.seleccionarImagenLayout);
        LinearLayout eliminarImagenLayout = view.findViewById(R.id.eliminarImagen);

        seleccionarImagenLayout.setOnClickListener(v -> {
            Log.d("PerfilBottomsheet", "Clic en seleccionar imagen");
            Toast.makeText(appContext, "Clic detectado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        eliminarImagenLayout.setOnClickListener(v -> {
            Log.d("PerfilBottomsheet", "Clic en eliminar imagen");
            eliminarImagenDeSupabase();
            dismiss();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Método de respaldo (aunque se use ActivityResultLauncher)
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Log.d("PerfilBottomsheet", "onActivityResult - Imagen seleccionada: " + imageUri);
            if (imageUri != null) {
                subirImagenASupabase(imageUri);
                dismiss();
            }
        }
    }

    private void subirImagenASupabase(Uri uri) {
        Log.d("PerfilBottomsheet", "subirImagenASupabase: Comenzando proceso de subida para: " + uri);
        OkHttpClient client = Supabase.getClient();

        // Obtener el email del usuario desde SharedPreferences usando el appContext guardado
        SharedPreferences preferences = appContext.getSharedPreferences("Sesion", Activity.MODE_PRIVATE);

        if (!Login.esUsuarioAutenticado(appContext)) {
            Log.e("PerfilBottomsheet", "Usuario no autenticado para subir imagen");
            showToast("Solo los usuarios registrados pueden subir imágenes");
            return;
        }
        String userEmail = preferences.getString("userEmail", "");
        if (userEmail == null || !userEmail.contains("@")) {
            String accessToken = preferences.getString("accessToken", "");
            Log.d("PerfilBottomsheet", "Intentando extraer email del token, token: " + accessToken);
            Toast.makeText(appContext, "Access Token: " + accessToken, Toast.LENGTH_LONG).show();
            userEmail = getEmailFromToken(accessToken);
        }
        Log.d("PerfilBottomsheet", "Email del usuario: " + userEmail);

        // Se usa la parte antes del '@' para generar el nombre de archivo
        String username = userEmail.split("@")[0].replaceAll("[^a-zA-Z0-9]", "_");
        String fileName = username + ".jpg";
        Log.d("PerfilBottomsheet", "Nombre de archivo generado: " + fileName);

        try (InputStream inputStream = appContext.getContentResolver().openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                Log.e("PerfilBottomsheet", "Error al decodificar bitmap desde: " + uri);
                showToast("Error al decodificar la imagen");
                return;
            }

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteStream);
            byte[] imageData = byteStream.toByteArray();
            bitmap.recycle();

            // Se añade upsert=true en la URL y el header "x-upsert" para forzar la sustitución.
            String uploadUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/"
                    + BUCKET_NAME + "/" + fileName + "?upsert=true";
            Log.d("PerfilBottomsheet", "Subiendo imagen a: " + uploadUrl);

            Request request = new Request.Builder()
                    .url(uploadUrl)
                    .header("Authorization", "Bearer " + Supabase.getSupabaseKey())
                    .header("Content-Type", IMAGE_TYPE)
                    .header("x-upsert", "true")
                    .post(RequestBody.create(imageData, MediaType.parse(IMAGE_TYPE)))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("PerfilBottomsheet", "Error en la subida de imagen: " + e.getMessage());
                    new Handler(Looper.getMainLooper()).post(() ->
                            showToast("Error de red: " + e.getMessage())
                    );
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.d("PerfilBottomsheet", "Respuesta de subida de imagen, código: " + response.code());
                    if (response.isSuccessful()) {
                        String imageUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/public/"
                                + BUCKET_NAME + "/" + fileName;
                        Log.d("PerfilBottomsheet", "Imagen subida correctamente. URL: " + imageUrl);
                        new Handler(Looper.getMainLooper()).post(() -> actualizarPerfilConImagen(imageUrl));
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Sin detalles";
                        Log.e("PerfilBottomsheet", "Error al subir imagen: " + response.code() + " - " + errorBody);
                        new Handler(Looper.getMainLooper()).post(() ->
                                showToast("Error al subir: " + response.code() + " - " + errorBody)
                        );
                    }
                    response.close();
                }
            });

        } catch (IOException e) {
            Log.e("PerfilBottomsheet", "IOException en subirImagenASupabase: " + e.getMessage());
            showToast("Error: " + e.getMessage());
        }
    }

    private String getEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return "";
            String payload = new String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
            org.json.JSONObject jsonObject = new org.json.JSONObject(payload);
            String email = jsonObject.optString("email", "");
            Log.d("JWT_DEBUG", "Decoded email: " + email);
            return email;
        } catch (Exception e) {
            Log.e("JWT_DEBUG", "Error decodificando token: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    // Usamos el appContext para mostrar Toasts sin depender del fragmento adjunto.
    private void showToast(String message) {
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
    }

    // Actualiza la URL en la tabla "perfiles" usando el contexto guardado.
    private void actualizarPerfilConImagen(String imageUrl) {
        Log.d("PerfilBottomsheet", "actualizarPerfilConImagen: Actualizando perfil con imagen URL: " + imageUrl);
        // Usamos appContext, que es el Context de la aplicación, para obtener las preferencias
        SharedPreferences preferences = appContext.getSharedPreferences("Sesion", Activity.MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        String accessToken = preferences.getString("accessToken", "");
        if (userId.isEmpty() || accessToken.isEmpty()) {
            Log.e("PerfilBottomsheet", "No se pudo obtener el usuario para actualizar imagen");
            return;
        }

        // Si todavía hay actividad, mostramos un diálogo de progreso; si no, solo registramos el proceso.
        boolean showUI = (getActivity() != null);
        ProgressDialog progressDialog;
        if (showUI) {
            progressDialog = new ProgressDialog(appContext);
            progressDialog.setMessage("Actualizando imagen de perfil...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else {
            progressDialog = null;
            Log.d("PerfilBottomsheet", "Fragment desacoplado, se ejecuta la actualización sin UI.");
        }

        OkHttpClient client = Supabase.getClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonBody = "{\"img_user\": \"" + imageUrl + "\"}";
        String updateUrl = Supabase.getSupabaseUrl() + "/rest/v1/perfiles?auth_id=eq." + userId;
        Log.d("PerfilBottomsheet", "Enviando petición PATCH a: " + updateUrl);

        Request request = new Request.Builder()
                .url(updateUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("apikey", Supabase.getSupabaseKey())
                .addHeader("Content-Type", "application/json")
                .method("PATCH", RequestBody.create(jsonBody, JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("PerfilBottomsheet", "Error al actualizar imagen del perfil: " + e.getMessage());
                if (showUI && getActivity() != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (progressDialog != null) progressDialog.dismiss();
                        showToast("Error al actualizar imagen de perfil: " + e.getMessage());
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("PerfilBottomsheet", "Respuesta de actualizar perfil, código: " + response.code());
                if (showUI && getActivity() != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (response.isSuccessful()) {
                            Log.d("PerfilBottomsheet", "Imagen de perfil actualizada correctamente en la base de datos.");
                            showToast("Imagen actualizada correctamente");
                            // Redirigir a Perfil2
                            Intent intent = new Intent(appContext, Perfil2.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            appContext.startActivity(intent);
                        } else {
                            Log.e("PerfilBottomsheet", "Error al actualizar en base de datos: " + response.code());
                            showToast("Error al actualizar en base de datos: " + response.code());
                        }
                    });
                } else {
                    if (response.isSuccessful()) {
                        Log.d("PerfilBottomsheet", "Imagen de perfil actualizada correctamente en la base de datos (sin UI).");
                    } else {
                        Log.e("PerfilBottomsheet", "Error al actualizar en base de datos (sin UI): " + response.code());
                    }
                }
                response.close();
            }
        });
    }

    private void eliminarImagenDeSupabase() {
        SharedPreferences preferences = appContext.getSharedPreferences("Sesion", Activity.MODE_PRIVATE);
        String userEmail = preferences.getString("userEmail", "");
        String accessToken = preferences.getString("accessToken", "");
        String userId = preferences.getString("userId", "");

        if (userEmail.isEmpty() || userId.isEmpty() || accessToken.isEmpty()) {
            Log.e("PerfilBottomsheet", "Faltan credenciales para eliminar imagen");
            showToast("No se pudo eliminar la imagen. Intenta iniciar sesión nuevamente.");
            return;
        }

        String username = userEmail.split("@")[0].replaceAll("[^a-zA-Z0-9]", "_");
        String fileName = username + ".jpg";
        String deleteUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName;

        OkHttpClient client = Supabase.getClient();
        Request request = new Request.Builder()
                .url(deleteUrl)
                .delete()
                .addHeader("Authorization", "Bearer " + Supabase.getSupabaseKey())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("PerfilBottomsheet", "Error al eliminar imagen: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        showToast("Error de red al eliminar imagen: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("PerfilBottomsheet", "Respuesta al eliminar imagen: " + response.code());
                if (response.isSuccessful()) {
                    // Imagen eliminada del bucket, ahora limpiar campo en tabla perfiles
                    limpiarCampoImagen(userId, accessToken);
                    // Redirigir a Perfil2
                    Intent intent = new Intent(appContext, Perfil2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    appContext.startActivity(intent);
                } else {
                    String body = response.body() != null ? response.body().string() : "Sin contenido";
                    Log.e("PerfilBottomsheet", "Error al eliminar imagen: " + body);
                    new Handler(Looper.getMainLooper()).post(() ->
                            showToast("Error al eliminar la imagen: " + response.code())
                    );
                }
                response.close();
            }
        });
    }
    private void limpiarCampoImagen(String userId, String accessToken) {
        OkHttpClient client = Supabase.getClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonBody = "{\"img_user\": null}";
        String updateUrl = Supabase.getSupabaseUrl() + "/rest/v1/perfiles?auth_id=eq." + userId;

        Request request = new Request.Builder()
                .url(updateUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("apikey", Supabase.getSupabaseKey())
                .addHeader("Content-Type", "application/json")
                .method("PATCH", RequestBody.create(jsonBody, JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("PerfilBottomsheet", "Error al limpiar campo de imagen: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        showToast("No se pudo limpiar la imagen de perfil")
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("PerfilBottomsheet", "Campo img_user actualizado. Código: " + response.code());
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.isSuccessful()) {
                        showToast("Imagen de perfil eliminada");
                        // Redirigir a Perfil2
                        Intent intent = new Intent(appContext, Perfil2.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        appContext.startActivity(intent);
                    } else {
                        showToast("Error al actualizar perfil: " + response.code());
                    }
                });
                response.close();
            }
        });
    }

}
