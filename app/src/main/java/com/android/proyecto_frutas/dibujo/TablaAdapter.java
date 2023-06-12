package com.android.proyecto_frutas.dibujo;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.proyecto_frutas.R;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class TablaAdapter extends RecyclerView.Adapter<TablaAdapter.TablaViewHolder> {
    private List<DataSnapshot> jugadores;

    public TablaAdapter(List<DataSnapshot> jugadores) {
        this.jugadores = jugadores;
    }

    @NonNull
    @Override
    public TablaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new TablaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TablaViewHolder holder, int position) {
        DataSnapshot jugadorSnapshot = jugadores.get(position);
        //String nombre = jugadorSnapshot.child("nombre").getValue(String.class);
        String nombre = jugadorSnapshot.getKey();
        int puntaje = jugadorSnapshot.child("score").getValue(Integer.class);

        holder.textViewPosicion.setText(String.valueOf(position + 1));
        holder.textViewNombre.setText(nombre);
        holder.textViewPuntaje.setText(String.valueOf(puntaje));
    }

    @Override
    public int getItemCount() {
        return jugadores.size();
    }

    public class TablaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPosicion;
        TextView textViewNombre;
        TextView textViewPuntaje;

        public TablaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPosicion = itemView.findViewById(R.id.textViewPosicion);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewPuntaje = itemView.findViewById(R.id.textViewPuntaje);
        }
    }
}
