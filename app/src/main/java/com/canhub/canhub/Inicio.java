package com.canhub.canhub;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canhub.canhub.databinding.ActivityInicioBinding;

import java.util.ArrayList;
import java.util.List;

public class Inicio extends AppCompatActivity {

    private ActivityInicioBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        LinearLayout contentedCarts = findViewById(R.id.contenedorCartas);

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
            agregarEscuela(contentedCarts, escuela);
        }

    }

    private void agregarEscuela(LinearLayout contender, Escuela escuela){
        View cartaView = getLayoutInflater().inflate(R.layout.item_escuela, contender, false);

        TextView title = cartaView.findViewById(R.id.nombreEscuela);
        TextView description = cartaView.findViewById(R.id.descripcionEscuela);
        ImageView image = cartaView.findViewById(R.id.imagenEscuela);

        title.setText(escuela.getNombre());
        description.setText(escuela.getDescripcion());
        image.setImageResource(escuela.getImagenResId());

        contender.addView(cartaView);
    }
}