package com.canhub.canhub;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GraficaJson extends AppCompatActivity {

    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grafica_json);

        lineChart = findViewById(R.id.lineChart);

        String jsonStr = "D://tiempo_altitud.json";

        try {
            JSONArray jsonArray = new JSONArray(jsonStr);

            List<Entry> entries = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                float tiempo = (float) obj.getDouble("tiempo");
                float altitud = (float) obj.getDouble("altitud");
                entries.add(new Entry(tiempo, altitud));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Altitud vs Tiempo");
            dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
            dataSet.setValueTextColor(getResources().getColor(android.R.color.black));

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.invalidate(); // Refrescar

        } catch (Exception e) {
            e.printStackTrace();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}