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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PerfilBottomsheet extends BottomSheetDialogFragment {

    private static final String IMAGE_TYPE = "image/jpeg";
    private static final String BUCKET_NAME = "imagenusuario";
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Context appContext;
    private OnPerfilUpdateListener perfilUpdateListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context.getApplicationContext();
        if (context instanceof OnPerfilUpdateListener) {
            perfilUpdateListener = (OnPerfilUpdateListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        perfilUpdateListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_perfil_bottomsheet, container, false);

        // Configuración del ActivityResultLauncher para la selección de imagen
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            subirImagenASupabase(imageUri);
                        }
                    }
                }
        );

        LinearLayout seleccionarImagenLayout = view.findViewById(R.id.seleccionarImagenLayout);
        LinearLayout eliminarImagenLayout = view.findViewById(R.id.eliminarImagen);

        seleccionarImagenLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        eliminarImagenLayout.setOnClickListener(v -> eliminarImagenDeSupabase());

        return view;
    }

    private void subirImagenASupabase(Uri uri) {
        OkHttpClient client = Supabase.getClient();
        SharedPreferences preferences = appContext.getSharedPreferences("Sesion", Activity.MODE_PRIVATE);

        if (!Login.esUsuarioAutenticado(appContext)) {
            showToast("Solo los usuarios registrados pueden subir imágenes");
            return;
        }

        String userEmail = preferences.getString("userEmail", "");
        if (userEmail == null || !userEmail.contains("@")) {
            String accessToken = preferences.getString("accessToken", "");
            userEmail = getEmailFromToken(accessToken);
        }

        String username = userEmail.split("@")[0].replaceAll("[^a-zA-Z0-9]", "_");
        String fileName = username + ".jpg";

        try (InputStream inputStream = appContext.getContentResolver().openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                showToast("Error al decodificar la imagen");
                return;
            }
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteStream);
            byte[] imageData = byteStream.toByteArray();
            bitmap.recycle();

            String uploadUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/"
                    + BUCKET_NAME + "/" + fileName + "?upsert=true";

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
                    new Handler(Looper.getMainLooper()).post(() ->
                            showToast("Error al subir: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String imageUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/public/"
                                + BUCKET_NAME + "/" + fileName;
                        new Handler(Looper.getMainLooper()).post(() -> actualizarPerfilConImagen(imageUrl));
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Sin detalles";
                        new Handler(Looper.getMainLooper()).post(() ->
                                showToast("Error al subir: " + response.code() + " - " + errorBody));
                    }
                    response.close();
                }
            });
        } catch (IOException e) {
            showToast("Error: " + e.getMessage());
        }
    }

    private String getEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3)
                return "";
            String payload = new String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
            JSONObject jsonObject = new JSONObject(payload);
            return jsonObject.optString("email", "");
        } catch (Exception e) {
            return "";
        }
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarPerfilConImagen(String imageUrl) {
        SharedPreferences preferences = appContext.getSharedPreferences("Sesion", Activity.MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        String accessToken = preferences.getString("accessToken", "");
        if (userId.isEmpty() || accessToken.isEmpty())
            return;

        boolean showUI = (getActivity() != null);
        ProgressDialog progressDialog;
        if (showUI) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Actualizando imagen de perfil...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else {
            progressDialog = null;
        }

        OkHttpClient client = Supabase.getClient();
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        String jsonBody = "{\"img_user\":\"" + imageUrl + "\"}";
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
                if (showUI && getActivity()!= null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        showToast("Error al actualizar imagen: " + e.getMessage());
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (showUI && getActivity() != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            showToast("La imagen puede tardar en actualizarse.");
                            dismiss();
                            if (perfilUpdateListener != null)
                                perfilUpdateListener.onPerfilUpdated();
                        } else {
                            showToast("Error al actualizar: " + response.code());
                        }
                    });
                } else {
                    if (response.isSuccessful() && getActivity() != null && perfilUpdateListener != null)
                        perfilUpdateListener.onPerfilUpdated();
                }
                response.close();
            }
        });
    }

    private void eliminarImagenDeSupabase() {
        // Primero, obtener el perfil desde Supabase usando el userId (o auth_id).
        SharedPreferences preferences = appContext.getSharedPreferences("Sesion", Activity.MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        String accessToken = preferences.getString("accessToken", "");

        if (userId.isEmpty() || accessToken.isEmpty()) {
            showToast("No se pudo autenticar");
            return;
        }

        // URL para consultar el campo img_user del perfil del usuario
        String queryUrl = Supabase.getSupabaseUrl() + "/rest/v1/perfiles?select=img_user&auth_id=eq." + userId;
        Request queryRequest = new Request.Builder()
                .url(queryUrl)
                .get()
                .addHeader("apikey", Supabase.getSupabaseKey())
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        OkHttpClient client = Supabase.getClient();
        client.newCall(queryRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        showToast("Error al consultar perfil: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);
                        if (jsonArray.length() == 0) {
                            new Handler(Looper.getMainLooper()).post(() ->
                                    showToast("No se encontró perfil."));
                            return;
                        }
                        JSONObject perfil = jsonArray.getJSONObject(0);
                        String imageUrl = perfil.optString("img_user", "");
                        if (imageUrl.isEmpty() || imageUrl.equalsIgnoreCase("null")) {
                            new Handler(Looper.getMainLooper()).post(() ->
                                    showToast("No hay imagen de perfil para eliminar."));
                        } else {
                            // Extrae el nombre del archivo de la URL guardada
                            String[] parts = imageUrl.split("/");
                            String fileName = parts[parts.length - 1];
                            // Llama al método que realiza la eliminación en Storage
                            eliminarImagen(fileName);
                        }
                    } catch (JSONException e) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                showToast("Error al procesar la respuesta del perfil."));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            showToast("Error al consultar el perfil: " + response.code()));
                }
                response.close();
            }
        });
    }

    private void eliminarImagen(String fileName) {
        String deleteUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/imagenusuario/" + fileName;
        OkHttpClient client = Supabase.getClient();

        Request request = new Request.Builder()
                .url(deleteUrl)
                .delete()
                .addHeader("Authorization", "Bearer " + Supabase.getSupabaseKey())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        showToast("Error al eliminar imagen: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() -> actualizarPerfilSinImagen());
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Sin detalles";
                    new Handler(Looper.getMainLooper()).post(() ->
                            showToast("Error al eliminar imagen: " + response.code() + " - " + errorBody));
                }
                response.close();
            }
        });
    }



    private void actualizarPerfilSinImagen() {
        SharedPreferences preferences = appContext.getSharedPreferences("Sesion", Activity.MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        String accessToken = preferences.getString("accessToken", "");
        if (userId.isEmpty() || accessToken.isEmpty())
            return;

        boolean showUI = (getActivity() != null);
        ProgressDialog progressDialog;
        if (showUI) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Eliminando imagen de perfil...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else {
            progressDialog = null;
        }

        OkHttpClient client = Supabase.getClient();
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
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
                if (showUI && getActivity() != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        showToast("Error al actualizar perfil: " + e.getMessage());
                    });
                }
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (showUI && getActivity() != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            showToast("Imagen eliminada correctamente");
                            dismiss();
                            if (perfilUpdateListener != null)
                                perfilUpdateListener.onPerfilUpdated();
                        } else {
                            showToast("Error al actualizar: " + response.code());
                        }
                    });
                } else {
                    if (response.isSuccessful() && getActivity() != null && perfilUpdateListener != null)
                        perfilUpdateListener.onPerfilUpdated();
                }
                response.close();
            }
        });
    }
}
