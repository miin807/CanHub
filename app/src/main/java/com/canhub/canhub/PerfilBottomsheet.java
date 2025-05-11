package com.canhub.canhub;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
    private  Uri selectedImageUri;
    private static final String BUCKET_NAME = "iamgenusuario";
    private ActivityResultLauncher<Intent> imagePickerLauncher;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_perfil_bottomsheet, container, false);

        // Inicializa el launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            subirImagenASupabase(imageUri);
                            dismiss();
                        }
                    }
                });

        LinearLayout seleccionarImagenLayout = view.findViewById(R.id.seleccionarImagenLayout);
        LinearLayout eliminarImagenLayout = view.findViewById(R.id.eliminarImagen);

        seleccionarImagenLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Clic detectado", Toast.LENGTH_SHORT).show(); // debug
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

            intent.setType("image/*");
            imagePickerLauncher.launch(intent); // ✅ Nuevo método
        });

        eliminarImagenLayout.setOnClickListener(v -> {
            eliminarImagenDeSupabase();
            dismiss();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                subirImagenASupabase(imageUri); // lógica de subida
                dismiss();
            }
        }
    }

    private void subirImagenASupabase(Uri uri) {
        OkHttpClient client = Supabase.getClient();

        // Obtener el email del usuario
        SharedPreferences preferences = requireContext().getSharedPreferences("Sesion", Activity.MODE_PRIVATE);

        if (!Login.esUsuarioAutenticado(requireContext())) {
            showToast("Solo los usuarios registrados pueden subir imágenes");
            return;
        }
        String userEmail = preferences.getString("userEmail", "");
        if (userEmail == null || !userEmail.contains("@")) {
            String accessToken = preferences.getString("accessToken", "");
            Toast.makeText(requireContext(), "Access Token: " + accessToken, Toast.LENGTH_LONG).show();
            userEmail = getEmailFromToken(accessToken);
        }



        // Usar parte del email antes del @ como nombre de archivo, limpiando caracteres
        String username = userEmail.split("@")[0].replaceAll("[^a-zA-Z0-9]", "_");
        String fileName = username + ".jpg";

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                showToast("Error al decodificar la imagen");
                return;
            }

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteStream);
            byte[] imageData = byteStream.toByteArray();
            bitmap.recycle();

            Request request = new Request.Builder()
                    .url(Supabase.getSupabaseUrl() + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName)
                    .header("Authorization", "Bearer " + Supabase.getSupabaseKey())
                    .header("Content-Type", IMAGE_TYPE)
                    .post(RequestBody.create(imageData, MediaType.parse(IMAGE_TYPE)))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    requireActivity().runOnUiThread(() -> showToast("Error de red: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String imageUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;
                        requireActivity().runOnUiThread(() -> actualizarPerfilConImagen(imageUrl));
                    } else if (isAdded()) {
                        String errorBody = response.body() != null ? response.body().string() : "Sin detalles";
                        requireActivity().runOnUiThread(() -> showToast("Error al subir: " + response.code() + " - " + errorBody));
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
            if (parts.length != 3) return "";

            String payload = new String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
            org.json.JSONObject jsonObject = new org.json.JSONObject(payload);

            String email = jsonObject.optString("email", "");
            android.util.Log.d("JWT_DEBUG", "Decoded email: " + email);
            return email;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void actualizarPerfilConImagen(String imageUrl) {
        // Aquí haces un POST o PATCH a tu tabla de usuarios para actualizar el campo "foto_url"
    }


    private void eliminarImagenDeSupabase() {
        // Lógica para borrar la imagen del Supabase Storage y actualizar el campo del usuario
    }
}

