package com.android.proyecto_frutas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.proyecto_frutas.adapters.SliderAdapter;
import com.android.proyecto_frutas.dibujo.MainActivity_Dibujo_nivel_1;
import com.android.proyecto_frutas.models.Slide;
import com.android.proyecto_frutas.tiempo.MainActivity_Tiempo_Nivel1;
import com.android.proyecto_frutas.tradicional.MainActivity_Nivel1;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText et_nombre;
    private ViewPager2 viewPager;
    private LinearLayout layoutDots;
    private SliderAdapter sliderAdapter;
    private List<Slide> slideList;
    private DatabaseReference databaseReference;
    private TextView tv_bestScore;
    private MediaPlayer mp;
    private Handler sliderHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_nombre = findViewById(R.id.txt_nombre);
        viewPager = findViewById(R.id.viewPager);
        layoutDots = findViewById(R.id.layoutDots);
        tv_bestScore = findViewById(R.id.textView_BestScore);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_logo);

        // Obtener referencia a la base de datos de Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("puntaje");

        // Obtén el mejor puntaje del usuario actual
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

        // Esta es la opción para ver los resultados de la tabla de Firebase
        resultTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                mp.release();

                // Detiene el cambio automático de imágenes del ViewPager
                stopSlideShow();

                Intent intent = new Intent(MainActivity.this, MainActivity_Dibujo_nivel_1.class);
                startActivity(intent);
                finish();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        // Crea una lista de imágenes para el slider
        slideList = new ArrayList<>();
        slideList.add(new Slide(R.drawable.icono_fruta_01));
        slideList.add(new Slide(R.drawable.icono_fruta_02));
        slideList.add(new Slide(R.drawable.icono_fruta_03));
        slideList.add(new Slide(R.drawable.icono_fruta_04));
        slideList.add(new Slide(R.drawable.icono_fruta_06));
        slideList.add(new Slide(R.drawable.icono_fruta_07));
        slideList.add(new Slide(R.drawable.icono_fruta_09));
        slideList.add(new Slide(R.drawable.icono_fruta_10));
        slideList.add(new Slide(R.drawable.icono_fruta_11));
        slideList.add(new Slide(R.drawable.icono_fruta_12));
        slideList.add(new Slide(R.drawable.icono_fruta_13));
        slideList.add(new Slide(R.drawable.icono_fruta_14));
        // Agrega más imágenes según sea necesario

        // Crea el adaptador del slider
        sliderAdapter = new SliderAdapter(slideList);

        // Configura el ViewPager2 con el adaptador
        viewPager.setAdapter(sliderAdapter);

        // Agrega los indicadores de página
        addDotsIndicator();

        // Establece un listener para cambiar los indicadores de página cuando cambia el slider
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDotsIndicator(position);
            }
        });

        // ...

        // MÚSICA DEL JUEGO
        mp = MediaPlayer.create(this, R.raw.juego_normal);
        mp.start();
        mp.setLooping(true);

        // Inicia el cambio automático de imágenes del ViewPager cada 3 segundos
        sliderHandler = new Handler();
        startSlideShow();
    }

    public void Jugar(View view) {
        startGame(MainActivity_Nivel1.class);
    }

    // ...

    private void startGame(Class<?> gameActivityClass) {
        String nombre = et_nombre.getText().toString();

        if (!nombre.isEmpty()) {
            //mp.stop();
//            mp.release();

            // Detiene el cambio automático de imágenes del ViewPager
            stopSlideShow();

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

    private void addDotsIndicator() {
        ImageView[] dots = new ImageView[slideList.size()];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.cinco);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(8, 0, 8, 0);
            layoutDots.addView(dots[i], layoutParams);
        }

        if (dots.length > 0) {
            dots[0].setImageResource(R.drawable.adicion);
        }
    }

    private void updateDotsIndicator(int position) {
        ImageView[] dots = new ImageView[slideList.size()];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.ic_close);
        }

        if (dots.length > 0) {
            dots[position].setImageResource(R.drawable.ic_close);
        }
    }

    // Inicia el cambio automático de imágenes del ViewPager cada 3 segundos
    private void startSlideShow() {
        sliderHandler.postDelayed(sliderRunnable, 2000);
    }

    // Detiene el cambio automático de imágenes del ViewPager
    private void stopSlideShow() {
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    // Runnable para cambiar de imagen en el ViewPager cada 3 segundos
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int currentPosition = viewPager.getCurrentItem();
            int nextPosition = currentPosition + 1;

            if (nextPosition >= slideList.size()) {
                nextPosition = 0;
            }

            viewPager.setCurrentItem(nextPosition);
            sliderHandler.postDelayed(this, 3000);
        }
    };

}
