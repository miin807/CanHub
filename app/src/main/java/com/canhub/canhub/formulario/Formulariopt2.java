package com.canhub.canhub.formulario;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.canhub.canhub.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Formulariopt2 extends AppCompatActivity {
    ImageButton imgButton;


    private static final int PICK_JSON_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulariopt2);

        imgButton=findViewById(R.id.imagen_subir);

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarArchivo();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void seleccionarArchivo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo JSON"), PICK_JSON_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_JSON_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                subirArchivoFirestore(uri);
            }
        }
    }


    private void subirArchivoFirestore(Uri fileUri) {
        if (fileUri == null) return;

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageRef.child("json/" + System.currentTimeMillis() + ".json");

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> data = new HashMap<>();
                    data.put("fileUrl", uri.toString());
                    data.put("timestamp", FieldValue.serverTimestamp());

                    db.collection("jsonFiles").add(data)
                            .addOnSuccessListener(documentReference ->
                                    Log.d("Firestore", "Archivo subido con ID: " + documentReference.getId())
                            )
                            .addOnFailureListener(e ->
                                    Log.e("Firestore", "Error al subir a Firestore", e)
                            );
                }))
                .addOnFailureListener(e -> Log.e("Firebase Storage", "Error al subir archivo", e));
    }

    public void goFormulariopt3(View view) {
        Intent intent = new Intent(Formulariopt2.this,Formulariopt3.class);
        startActivity(intent);
    }

    public void goForm1(View view) {
        Intent intent = new Intent(Formulariopt2.this,Formulariopt1.class);
        startActivity(intent);
    }
}