package com.canhub.canhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EscuelaAdapter extends RecyclerView.Adapter<EscuelaAdapter.EscuelaViewHolder> {

    private List<Escuela> escuelas; // Lista de datos a mostrar

    // Constructor del adaptador
    public EscuelaAdapter(List<Escuela> escuelas) {
        this.escuelas = escuelas;
    }

    @NonNull
    @Override
    public EscuelaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño de la tarjeta (item_escuela.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_escuela, parent, false);
        return new EscuelaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EscuelaViewHolder holder, int position) {
        // Obtén el objeto com.canhub.canhub.Escuela correspondiente a la posición actual
        Escuela escuela = escuelas.get(position);

        // Llena los datos en las vistas
        holder.nombreEscuela.setText(escuela.getNombre());
        holder.descripcionEscuela.setText(escuela.getDescripcion());
        holder.imagenEscuela.setImageResource(escuela.getImagenResId());
    }

    @Override
    public int getItemCount() {
        return escuelas.size(); // Devuelve la cantidad de ítems
    }

    // Clase interna para el ViewHolder
    public static class EscuelaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreEscuela, descripcionEscuela;
        ImageView imagenEscuela;

        public EscuelaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Asocia las vistas del layout item_escuela.xml con sus IDs
            nombreEscuela = itemView.findViewById(R.id.nombreEscuela);
            descripcionEscuela = itemView.findViewById(R.id.descripcionEscuela);
            imagenEscuela = itemView.findViewById(R.id.imagenEscuela);
        }
    }
}

