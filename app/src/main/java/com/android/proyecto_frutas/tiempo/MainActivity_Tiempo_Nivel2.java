package com.android.proyecto_frutas.tiempo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.proyecto_frutas.MainActivity;
import com.android.proyecto_frutas.R;
import com.android.proyecto_frutas.modelo.NavegacionMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity_Tiempo_Nivel2 extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView tv_nombre, tv_score, tv_timer;
    private ImageView iv_Auno, iv_Ados, iv_vidas, imageView_signo;
    private EditText et_respuesta;
    private MediaPlayer mp, mp_great, mp_bad;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long timeLeftInMillis = 30000; // 30 segundos
    private static final long COUNTDOWN_INTERVAL = 1000; // Intervalo de actualización del temporizador

    private DatabaseReference databaseRef;

    private boolean isMenuOpen = false;

    int score, numAleatorio_uno, numAleatorio_dos, resultado, vidas = 3;
    String nombre_jugador, string_score, string_vidas;

    String numero [] = {"cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tiempo_nivel2);

        Toast.makeText(this, "Nivel 2 - Sumas y Restas", Toast.LENGTH_SHORT).show();

        tv_nombre = findViewById(R.id.textView_nombre);
        tv_score = findViewById(R.id.textView_score);
        iv_vidas = findViewById(R.id.imageView_vidas);
        iv_Auno = findViewById(R.id.imageView_NumUno);
        iv_Ados = findViewById(R.id.imageView_NumDos);
        imageView_signo = findViewById(R.id.imageView_signo);
        et_respuesta = findViewById(R.id.editText_resultado);
        tv_timer = findViewById(R.id.textView_timer);

        nombre_jugador = getIntent().getStringExtra("jugador");
        tv_nombre.setText("Jugador: " + nombre_jugador);

        string_score = getIntent().getStringExtra("score");
        score = Integer.parseInt(string_score);
        tv_score.setText("Score: " + score);

        string_vidas = getIntent().getStringExtra("vidas");
        vidas = Integer.parseInt(string_vidas);
        if (vidas == 3) {
            iv_vidas.setImageResource(R.drawable.tresvidas);
        } else if (vidas == 2) {
            iv_vidas.setImageResource(R.drawable.dosvidas);
        } else if (vidas == 1) {
            iv_vidas.setImageResource(R.drawable.unavida);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_logo);

        

        mp_great = MediaPlayer.create(this, R.raw.wonderful);
        mp_bad = MediaPlayer.create(this, R.raw.bad);

        databaseRef = FirebaseDatabase.getInstance().getReference("puntaje");

        NumAleatorio();
        startTimer();

        // En tu actividad principal
        NavegacionMenu navegacionMenu = new NavegacionMenu(this, nombre_jugador);
        navegacionMenu.setupNavigationDrawer();
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void Comparar(View view) {
        String respuesta = et_respuesta.getText().toString();

        if (!respuesta.equals("")) {
            int respuesta_jugador = Integer.parseInt(respuesta);
            if (resultado == respuesta_jugador) {
                mp_great.start();
                score++;
                tv_score.setText("Score: " + score);
                et_respuesta.setText("");
                BaseDeDatos();
                NumAleatorio();
                resetTimer();
            } else {
                mp_bad.start();
                vidas--;
                BaseDeDatos();

                switch (vidas) {
                    case 3:
                        iv_vidas.setImageResource(R.drawable.tresvidas);
                        break;
                    case 2:
                        Toast.makeText(this, "Te quedan 2 manzanas", Toast.LENGTH_LONG).show();
                        iv_vidas.setImageResource(R.drawable.dosvidas);
                        break;
                    case 1:
                        Toast.makeText(this, "Te queda 1 manzana", Toast.LENGTH_LONG).show();
                        iv_vidas.setImageResource(R.drawable.unavida);
                        break;
                    case 0:
                        Toast.makeText(this, "Has perdido todas tus manzanas", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        mp.stop();
                        mp.release();
                        break;
                }

                et_respuesta.setText("");
                NumAleatorio();
                resetTimer();
            }
        } else {
            Toast.makeText(this, "Escribe tu respuesta", Toast.LENGTH_SHORT).show();
        }
    }

    public void NumAleatorio() {
        if (score <= 30) {
            numAleatorio_uno = (int) (Math.random() * 10);
            numAleatorio_dos = (int) (Math.random() * 10);

            if (numAleatorio_uno >= 0 && numAleatorio_uno <= 3) {
                resultado = numAleatorio_uno + numAleatorio_dos;
                imageView_signo.setImageResource(R.drawable.adicion);
            } else if (numAleatorio_uno >= 4 && numAleatorio_uno <= 7) {
                resultado = numAleatorio_uno - numAleatorio_dos;
                imageView_signo.setImageResource(R.drawable.resta);
            } else {
                NumAleatorio();
                return;
            }

            if (resultado >= 0) {
                for (int i = 0; i < numero.length; i++) {
                    int id = getResources().getIdentifier(numero[i], "drawable", getPackageName());
                    if (numAleatorio_uno == i) {
                        iv_Auno.setImageResource(id);
                    }
                    if (numAleatorio_dos == i) {
                        iv_Ados.setImageResource(id);
                    }
                }
            } else {
                NumAleatorio();
            }
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            Toast.makeText(this, "¡FELICIDADES ERES UN GENIO!", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
            mp.stop();
            mp.release();
        }
    }

    public void BaseDeDatos() {
        DatabaseReference jugadorRef = databaseRef.child(nombre_jugador);

        jugadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int bestScore = dataSnapshot.child("score").getValue(Integer.class);

                    if (score > bestScore) {
                        dataSnapshot.getRef().child("score").setValue(score);
                    }
                } else {
                    jugadorRef.child("score").setValue(score);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura/escritura de la base de datos
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountdownText();
                resetTimer();
                vidas--;
                BaseDeDatos();

                switch (vidas) {
                    case 3:
                        iv_vidas.setImageResource(R.drawable.tresvidas);
                        break;
                    case 2:
                        Toast.makeText(MainActivity_Tiempo_Nivel2.this, "Te quedan 2 manzanas", Toast.LENGTH_LONG).show();
                        iv_vidas.setImageResource(R.drawable.dosvidas);
                        break;
                    case 1:
                        Toast.makeText(MainActivity_Tiempo_Nivel2.this, "Te queda 1 manzana", Toast.LENGTH_LONG).show();
                        iv_vidas.setImageResource(R.drawable.unavida);
                        break;
                    case 0:
                        Toast.makeText(MainActivity_Tiempo_Nivel2.this, "Has perdido todas tus manzanas", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity_Tiempo_Nivel2.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        mp.stop();
                        mp.release();
                        break;
                }

                et_respuesta.setText("");
                NumAleatorio();
            }
        }.start();

        timerRunning = true;
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tv_timer.setText(timeLeftFormatted);
    }


    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeftInMillis = 15000;
        updateCountdownText();
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if (isMenuOpen) {
            closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    private void openMenu() {
        drawerLayout.openDrawer(GravityCompat.START);
        isMenuOpen = true;
    }

    private void closeMenu() {
        drawerLayout.closeDrawer(GravityCompat.START);
        isMenuOpen = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
