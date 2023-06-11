package com.android.proyecto_frutas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.proyecto_frutas.dibujo.MainActivity_Dibujo_nivel_1;
import com.android.proyecto_frutas.tiempo.MainActivity_Tiempo_Nivel1;
import com.android.proyecto_frutas.tradicional.MainActivity_Nivel1;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText et_nombre;
    private LottieAnimationView iv_personaje;
    private TextView tv_bestScore;
    private MediaPlayer mp;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_nombre = findViewById(R.id.txt_nombre);
        iv_personaje = findViewById(R.id.imageView_Personaje);
        tv_bestScore = findViewById(R.id.textView_BestScore);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        // Obtener referencia a la base de datos de Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("puntaje");

        // Obtener el mejor puntaje del usuario actual
        obtenerMejorPuntaje();

        // Obtener referencia a los botones de modo de juego
        Button traditionalModeButton = findViewById(R.id.button_traditional_mode);
        Button voiceModeButton = findViewById(R.id.button_voice_mode);
        Button resultTableButton = findViewById(R.id.button_result_mode);
        ImageButton settingsButton = findViewById(R.id.button_settings);

        traditionalModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(MainActivity_Nivel1.class);
            }
        });

        voiceModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(MainActivity_Tiempo_Nivel1.class);
            }
        });

        // Esta es la opcion para ver resultados de la tabla firebase
        resultTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(MainActivity_Dibujo_nivel_1.class);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        // ...

        // MUSICA DEL JUEGO
        mp = MediaPlayer.create(this, R.raw.alphabet_song);
        mp.start();
        mp.setLooping(true);
    }

    public void Jugar(View view) {
        startGame(MainActivity_Nivel1.class);
    }

    // ...

    private void startGame(Class<?> gameActivityClass) {
        String nombre = et_nombre.getText().toString();

        if (!nombre.isEmpty()) {
            mp.stop();
            mp.release();

            Intent intent = new Intent(this, gameActivityClass);
            intent.putExtra("jugador", nombre);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Primero debes escribir tu nombre", Toast.LENGTH_SHORT).show();

            et_nombre.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_nombre, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void openSettings() {
        Intent intent = new Intent(this, MainActivity_Nivel1.class);
        startActivity(intent);
    }

    // ...

    private void obtenerMejorPuntaje() {
        databaseReference.orderByChild("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int mejorPuntaje = 0;
                    String nombreMejorPuntaje = "";

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String nombre = snapshot.getKey();
                        int puntaje = snapshot.child("score").getValue(Integer.class);

                        if (puntaje > mejorPuntaje) {
                            mejorPuntaje = puntaje;
                            nombreMejorPuntaje = nombre;
                        }
                    }

                    tv_bestScore.setText("Mejor Puntaje: " + mejorPuntaje + " (" + nombreMejorPuntaje + ")");
                } else {
                    tv_bestScore.setText("Mejor Puntaje: 0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new android.app.AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_close)
                    .setTitle("Warning")
                    .setMessage("¿Quieres salir del juego?")
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    })
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
