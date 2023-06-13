package com.android.proyecto_frutas.estadisticas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.proyecto_frutas.MainActivity;
import com.android.proyecto_frutas.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity_Tabla extends AppCompatActivity {

    private ImageView iv_dibujo;
    private EditText et_respuesta;
    private Button btn_listo;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private Path path;

    private int[] inputPixels = new int[28 * 28];
    private RecyclerView recyclerView;
    private TablaAdapter tablaAdapter;
    private MediaPlayer mp;
    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Volver atrás utilizando el botón de retroceso nativo
        // Lógica para regresar al MainActivity
        Intent intent = new Intent(MainActivity_Tabla.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabla);

        Button btnRegresar = findViewById(R.id.btn_regresar);
        // MÚSICA DEL JUEGO
        mp = MediaPlayer.create(this, R.raw.ver_resultados2);
        mp.start();
        mp.setLooping(true);
        btnRegresar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mp.stop();
                mp.release();
                // Lógica para regresar al MainActivity
                Intent intent = new Intent(MainActivity_Tabla.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


            recyclerView = findViewById(R.id.recyclerViewTabla);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

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
                    // Ordenar la lista por score mayor primero
                    Collections.sort(jugadores, new Comparator<DataSnapshot>() {
                        @Override
                        public int compare(DataSnapshot dataSnapshot, DataSnapshot t1) {
                            int puntaje1 = dataSnapshot.child("score").getValue(Integer.class);
                            int puntaje2 = t1.child("score").getValue(Integer.class);
                            return Integer.compare(puntaje1,puntaje2);
                        }
                    });
                    Collections.reverse(jugadores);
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


    public void configurarLienzoDibujo(){
        // Configurar el lienzo de dibujo
        bitmap = Bitmap.createBitmap(480, 640, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(10);
        iv_dibujo.setImageBitmap(bitmap);
    }

}
