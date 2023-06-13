package com.android.proyecto_frutas.estadisticas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.proyecto_frutas.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TablaActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TablaAdapter tablaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabla);

        recyclerView = findViewById(R.id.recyclerViewTabla);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        List<DataSnapshot> jugadores = new ArrayList<>();
        // Obtener los datos de Firebase y agregarlos a la lista jugadores
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("puntaje");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Crear el arreglo dinámico de pares
            List<DataSnapshot> jugadores = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    jugadores.add(snapshot);
                }

                tablaAdapter = new TablaAdapter(jugadores);
                recyclerView.setAdapter(tablaAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejo de errores
            }
        });
        tablaAdapter = new TablaAdapter(jugadores);
        recyclerView.setAdapter(tablaAdapter);
    }
}
