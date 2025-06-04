package com.canhub.canhub.lanzamientos;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.canhub.canhub.PlantillaPerfil;
import com.canhub.canhub.R;
import com.canhub.canhub.Supabase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Altitud#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Altitud extends Fragment {
    private LineChart lineChart;
    private String nombrecentro;
    private String fecha;
    private static final String BUCKET_NAME_1 = "json";

    public static Altitud newInstance(Bundle args) {
        Altitud fragment = new Altitud();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            nombrecentro = getArguments().getString("nombrecentro");
            fecha = getArguments().getString("fecha");
        }

        // Leer el JSON y sacar grafica
        View view = inflater.inflate(R.layout.fragment_temperatura, container, false);
        lineChart = view.findViewById(R.id.lineChart);

        visualizarGrafica();
        return view;
    }

    public void visualizarGrafica() {
        // Aquí tu JSON (puedes cargarlo desde un archivo o API si quieres)
        new Thread(() -> {
            try {
                String jsonFileName = nombrecentro.replaceAll("[^a-zA-Z0-9]", "_") + "_" + fecha + ".json";
                String jsonUrl = Supabase.getSupabaseUrl() + "/storage/v1/object/public/" + BUCKET_NAME_1 + "/" + jsonFileName;

                Log.d("URL_JSON", jsonUrl); // para verificar si está bien formado

                URL url = new URL(jsonUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e("HTTP", "Error al obtener JSON: " + responseCode);
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                reader.close();
                conn.disconnect();

                JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
                ArrayList<Entry> entradas = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    float tiempo = (float) obj.getDouble("time");
                    float altitud = (float) obj.getDouble("altitude");

                    entradas.add(new Entry(tiempo, altitud));
                }

                LineDataSet dataSet = new LineDataSet(entradas, "Altitud (m)");
                dataSet.setColor(getResources().getColor(R.color.azul));
                dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
                dataSet.setLineWidth(2f);

                LineData lineData = new LineData(dataSet);

                requireActivity().runOnUiThread(() -> {
                    lineChart.setData(lineData);
                    lineChart.invalidate();

                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);

                    YAxis yAxis = lineChart.getAxisRight();
                    yAxis.setEnabled(false);

                    lineChart.setTouchEnabled(true);
                    lineChart.setHighlightPerTapEnabled(true);

                    CustomMarkerView marker = new CustomMarkerView(getContext(), R.layout.activity_custom_marker_view);
                    lineChart.setMarker(marker);

                    lineChart.setRenderer(new LineChartRenderer(lineChart, lineChart.getAnimator(), lineChart.getViewPortHandler()) {
                        @Override
                        public void drawExtras(Canvas c) {
                            super.drawExtras(c);
                            Paint paint = new Paint();
                            paint.setColor(Color.BLACK);
                            paint.setTextSize(15f);
                            paint.setTextAlign(Paint.Align.CENTER);
                            c.drawText("Tiempo(s)", lineChart.getWidth() / 2f, lineChart.getHeight() - 10f, paint);
                        }
                    });
                });

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Grafica", "Error: " + e.getMessage());
            }
        }).start();
    }
}