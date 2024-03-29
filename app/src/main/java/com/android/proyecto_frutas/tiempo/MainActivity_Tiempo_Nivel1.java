package com.android.proyecto_frutas.tiempo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.proyecto_frutas.MainActivity;
import com.android.proyecto_frutas.R;
import com.android.proyecto_frutas.modelo.NavegacionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity_Tiempo_Nivel1 extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView tv_nombre, tv_score, tv_timer;
    private ImageView iv_Auno, iv_Ados, iv_vidas;
    private EditText et_respuesta;
    private MediaPlayer mp, mp_great, mp_bad;
    private boolean isMenuOpen = false;

    int score, numAleatorio_uno, numAleatorio_dos, resultado, vidas = 3;
    String nombre_jugador, string_score, string_vidas;

    String numero[] = {"cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"};

    private DatabaseReference databaseRef;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 15000; // 15 segundos
    private boolean timerRunning;
    private static final long COUNTDOWN_IN_MILLIS = 15000; // 15 segundos

    private int defaultTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tiempo_nivel1);

        Toast.makeText(this, "Nivel 1 - Sumas básicas", Toast.LENGTH_SHORT).show();
        Button btnRegresar = findViewById(R.id.btn_regresar);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_Tiempo_Nivel1.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tv_nombre = findViewById(R.id.textView_nombre);
        tv_score = findViewById(R.id.textView_score);
        tv_timer = findViewById(R.id.textView_timer);
        iv_vidas = findViewById(R.id.imageView_vidas);
        iv_Auno = findViewById(R.id.imageView_NumUno);
        iv_Ados = findViewById(R.id.imageView_NumDos);
        et_respuesta = findViewById(R.id.editText_resultado);

        defaultTextColor = tv_timer.getCurrentTextColor();

        nombre_jugador = getIntent().getStringExtra("jugador");
        tv_nombre.setText("Jugador: " + nombre_jugador);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_logo);

        databaseRef = FirebaseDatabase.getInstance().getReference("puntaje");

        mp = MediaPlayer.create(this, R.raw.goats);
        mp_great = MediaPlayer.create(this, R.raw.wonderful);
        mp_bad = MediaPlayer.create(this, R.raw.bad);

        startTimer();
        NumAleatorio();
        pauseTimer();

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
            int respuesta_jugador;
            try {
                respuesta_jugador = Integer.parseInt(respuesta);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Respuesta inválida. Ingrese un número válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (resultado == respuesta_jugador) {
                mp_great.start();
                score++;
                tv_score.setText("Score: " + score);
                et_respuesta.setText("");
                BaseDeDatos();
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
            }
            resetTimer();
            startTimer();
            NumAleatorio();
        } else {
            Toast.makeText(this, "Escribe tu respuesta", Toast.LENGTH_SHORT).show();
            resetTimer();
            startTimer();
        }
    }

    public void NumAleatorio() {
        if (score <= 10) {
            numAleatorio_uno = (int) (Math.random() * 10);
            numAleatorio_dos = (int) (Math.random() * 10);
            resultado = numAleatorio_uno + numAleatorio_dos;

            if (resultado <= 10) {
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
            pauseTimer();
            Intent intent = new Intent(this, MainActivity_Tiempo_Nivel2.class);
            string_score = String.valueOf(score);
            string_vidas = String.valueOf(vidas);
            intent.putExtra("jugador", nombre_jugador);
            intent.putExtra("score", string_score);
            intent.putExtra("vidas", string_vidas);
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

    @Override
    public void onBackPressed() {
        // No hacer nada al presionar el botón de retroceso
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                closeMenu();
            } else {
                openMenu();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openMenu() {
        isMenuOpen = true;
        pauseTimer();
    }

    private void closeMenu() {
        isMenuOpen = false;
        resumeTimer();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void resumeTimer() {
        if (timerRunning) {
            startTimer();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();

                // Cambiar el color del círculo a beige cuando el tiempo sea mayor a 5 segundos
                if (millisUntilFinished >= 5000) {
                    tv_timer.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
                }
                // Cambiar el color del círculo a rojo cuando falten 5 segundos o menos
                else {
                    tv_timer.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }

            @Override
            public void onFinish() {
                // El temporizador ha terminado, se ejecuta el código correspondiente
                et_respuesta.setText("");
                Toast.makeText(MainActivity_Tiempo_Nivel1.this, "Tardaste mucho tiempo en responder", Toast.LENGTH_SHORT).show();
                et_respuesta.setText("999");
                Comparar(findViewById(R.id.button2));
            }
        }.start();

        timerRunning = true;
    }

    private void resetTimer() {
        countDownTimer.cancel();
        timeLeftInMillis = COUNTDOWN_IN_MILLIS;
        updateTimerText();
        tv_timer.setTextColor(defaultTextColor);
        timerRunning = false;
    }

    private void updateTimerText() {
        int seconds = (int) (timeLeftInMillis / 1000) + 1; // Agrega 1 segundo para compensar el retardo
        String timeLeftFormatted = String.valueOf(seconds);
        tv_timer.setText(timeLeftFormatted);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timerRunning) {
            startTimer();
        }
    }
}
