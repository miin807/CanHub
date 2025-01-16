package com.canhub.canhub;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canhub.canhub.databinding.ActivityEscuelasScrollBinding;
import com.canhub.canhub.databinding.ActivityInicioBinding;

import java.util.ArrayList;
import java.util.List;

public class Inicio extends AppCompatActivity {

    private ActivityInicioBinding binding;
    TextView titulo, descrip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        LinearLayout contenedorCartas = findViewById(R.id.contenedorCartas);

        List<Escuela> listaEscuelas = new ArrayList<>();
        listaEscuelas.add(new Escuela("IES Juan de la Cierva", "Lorem ipsum dolor sit amet.", R.drawable.logo1));
        listaEscuelas.add(new Escuela("IES Juan de la Cierva", "Lorem ipsum dolor sit amet.", R.drawable.logo1));
        listaEscuelas.add(new Escuela("IES Juan de la Cierva", "Lorem ipsum dolor sit amet.", R.drawable.logo1));
        listaEscuelas.add(new Escuela("Institut de Terrassa", "Lorem ipsum dolor sit amet.", R.drawable.logo2));
        listaEscuelas.add(new Escuela("Institut de Terrassa", "Lorem ipsum dolor sit amet.", R.drawable.logo2));
        listaEscuelas.add(new Escuela("Institut de Terrassa", "Lorem ipsum dolor sit amet.", R.drawable.logo2));
        listaEscuelas.add(new Escuela("IES Príncipe Felipe", "Descripción adicional", R.drawable.logo3));
        listaEscuelas.add(new Escuela("IES Príncipe Felipe", "Descripción adicional", R.drawable.logo3));
        listaEscuelas.add(new Escuela("IES Príncipe Felipe", "Descripción adicional", R.drawable.logo3));

        for(Escuela escuela: listaEscuelas){
            agregarEscuela(contenedorCartas, escuela);
        }

    }

    private void agregarEscuela(LinearLayout contenedor, Escuela escuela){
        View cartaView = getLayoutInflater().inflate(R.layout.item_escuela, contenedor, false);

        TextView titulo = cartaView.findViewById(R.id.nombreEscuela);
        TextView descripcion = cartaView.findViewById(R.id.descripcionEscuela);
        ImageView imagen = cartaView.findViewById(R.id.imagenEscuela);

        titulo.setText(escuela.getNombre());
        descripcion.setText(escuela.getDescripcion());
        imagen.setImageResource(escuela.getImagenResId());

        contenedor.addView(cartaView);
    }
}