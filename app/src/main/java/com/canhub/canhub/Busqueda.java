package com.canhub.canhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        HashMap<String, List<ItemHijo>> listChildData = new HashMap<>();

        // Datos para 2024
        List<ItemHijo> childItems2024 = new ArrayList<>();
        childItems2024.add(new ItemHijo(R.drawable.juandelacierva, "IES Juan de la Cierva\nLorem ipsum dolor sit amet..."));
        childItems2024.add(new ItemHijo(R.drawable.terrassa, "Instituto de Terrassa\nLorem ipsum dolor sit amet..."));
        childItems2024.add(new ItemHijo(R.drawable.principe_felipe, "IES Príncipe Felipe\nLorem ipsum dolor sit amet..."));
        childItems2024.add(new ItemHijo(R.drawable.fernandoiii, "IES Fernando III de Jaén\nLorem ipsum dolor sit amet..."));

        // Agrega los datos de 2024 al mapa
        listChildData.put("2024", childItems2024);

        // Datos para otros años
        List<ItemHijo> childItems2023 = new ArrayList<>();
        childItems2023.add(new ItemHijo(R.drawable.juandelacierva, "IES Juan de la Cierva."));
        listChildData.put("2023", childItems2023);

        // Configura el adaptador
      com.canhub.canhub.CustomExpandableListAdapter adapter = new com.canhub.canhub.CustomExpandableListAdapter(this, listGroupTitles, listChildData);
        expandableListView.setAdapter(adapter);


        expandableListView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.boton_navegacion);
        bottomNavigationView.setSelectedItemId(R.id.biblioteca);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.inicio) {
                    Intent int1 = new Intent(Busqueda.this, Inicio.class);
                    int1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int1);
                }else if (item.getItemId()==R.id.menu){
                    Intent int2 = new Intent(Busqueda.this, Ajustes.class);
                    int2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(int2);
                }
                return false;
            }
        });


    }
}

