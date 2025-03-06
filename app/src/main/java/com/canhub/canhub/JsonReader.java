package com.canhub.canhub;

import static kotlinx.coroutines.CoroutineScopeKt.CoroutineScope;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.canhub.canhub.formulario.Formulariopt2;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import kotlinx.coroutines.CoroutineScope;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JsonReader {
    private float temperatura;
    private float presion;
    private float altitud;
    private Context context;

    public JsonReader(Context context) {
        this.context = context;
    }

    public void leerJsonDesdeUri(Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String jsonContenido = stringBuilder.toString();
            Log.d("JSON", "Contenido: " + jsonContenido);

            // Procesar y enviar a Supabase
            procesarJson(jsonContenido);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al leer el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void procesarJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            // Extraer datos del JSON
            this.temperatura = jsonObject.getLong("temperatura");
            this.presion = jsonObject.getLong("presion");
            this.altitud = jsonObject.getLong("altitud");

            Log.d("JSON", "Temperatura: " + temperatura + ", Presion: " + presion + ", Altitud: " + altitud);


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error en formato JSON", Toast.LENGTH_SHORT).show();
        }
    }

    public float getTemperatura() {
        return temperatura;
    }

    public float getPresion() {
        return presion;
    }

    public float getAltitud() {
        return altitud;
    }
}