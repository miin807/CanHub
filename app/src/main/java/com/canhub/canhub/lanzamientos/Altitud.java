package com.canhub.canhub.lanzamientos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.canhub.canhub.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Altitud#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Altitud extends Fragment {
    private LineChart lineChart;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Altitud() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Altitud.
     */
    // TODO: Rename and change types and number of parameters
    public static Altitud newInstance(String param1, String param2) {
        Altitud fragment = new Altitud();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperatura, container, false);

        // Leer el JSON y sacar grafica

        lineChart = view.findViewById(R.id.lineChart);
        visualizarGrafica();
        return view;
    }

    public void visualizarGrafica() {
        // Aquí tu JSON (puedes cargarlo desde un archivo o API si quieres)

        try {
            InputStream is = getActivity().getAssets().open("jsonPrueba.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(jsonStr);

            ArrayList<Entry> entradas = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                float tiempo = (float) obj.getDouble("tiempo_s");
                float temperatura = (float) obj.getDouble("altitud_m");

                entradas.add(new Entry(tiempo, temperatura)); // eje X: tiempo, eje Y: temperatura
            }

            LineDataSet dataSet = new LineDataSet(entradas, "Altitud");
            dataSet.setColor(getResources().getColor(android.R.color.holo_red_light));
            dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
            dataSet.setLineWidth(2f);

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.invalidate(); // refrescar

            // Opcional: configuración del eje X
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            // Opcional: configuración del eje Y
            YAxis yAxis = lineChart.getAxisRight();
            yAxis.setEnabled(false);

            // Tooltips (al tocar un punto)
            lineChart.setTouchEnabled(true);
            lineChart.setHighlightPerTapEnabled(true);

            CustomMarkerView marker = new CustomMarkerView(getContext(), R.layout.activity_custom_marker_view); // ✅ BIEN
            lineChart.setMarker(marker);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}