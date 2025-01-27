package com.canhub.canhub;

import android.os.Bundle;
import android.widget.ExpandableListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Busqueda extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        // Inicializa el ExpandableListView
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        // Define los datos del grupo y los hijos
        List<String> listGroupTitles = new ArrayList<>();
        listGroupTitles.add("2024");
        listGroupTitles.add("2023");
        listGroupTitles.add("2022");
        listGroupTitles.add("2021");
        listGroupTitles.add("2020");

        HashMap<String, List<ChildItem>> listChildData = new HashMap<>();

        // Datos para 2024
        List<ChildItem> childItems2024 = new ArrayList<>();
        childItems2024.add(new ChildItem(R.drawable.juandelacierva, "IES Juan de la Cierva\nLorem ipsum dolor sit amet..."));
        childItems2024.add(new ChildItem(R.drawable.terrassa, "Instituto de Terrassa\nLorem ipsum dolor sit amet..."));
        childItems2024.add(new ChildItem(R.drawable.principe_felipe, "IES Príncipe Felipe\nLorem ipsum dolor sit amet..."));
        childItems2024.add(new ChildItem(R.drawable.fernandoiii, "IES Fernando III de Jaén\nLorem ipsum dolor sit amet..."));

        // Agrega los datos de 2024 al mapa
        listChildData.put("2024", childItems2024);

        // Datos para otros años (puedes agregar más de manera similar)
        List<ChildItem> childItems2023 = new ArrayList<>();
        childItems2023.add(new ChildItem(R.drawable.juandelacierva, "IES Juan de la Cierva."));
        listChildData.put("2023", childItems2023);

        // Configura el adaptador
        com.example.expandablelistviewdemo.CustomExpandableListAdapter adapter = new com.example.expandablelistviewdemo.CustomExpandableListAdapter(this, listGroupTitles, listChildData);
        expandableListView.setAdapter(adapter);
    }



}
