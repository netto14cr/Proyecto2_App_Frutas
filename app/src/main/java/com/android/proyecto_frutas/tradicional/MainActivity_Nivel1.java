package com.android.proyecto_frutas.tradicional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.proyecto_frutas.MainActivity;
import com.android.proyecto_frutas.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity_Nivel1 extends AppCompatActivity {

    private TextView tv_nombre, tv_score;
    private ImageView iv_Auno, iv_Ados, iv_vidas;
    private EditText et_respuesta;
    private MediaPlayer mp, mp_great, mp_bad;

    int score, numAleatorio_uno, numAleatorio_dos, resultado, vidas = 3;
    String nombre_jugador, string_score, string_vidas;

    String numero [] = {"cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};

    // Agrega la referencia a la base de datos de Firebase Realtime
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nivel1);
        Button btnRegresar = findViewById(R.id.btn_regresar);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para regresar al MainActivity
                Intent intent = new Intent(MainActivity_Nivel1.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Toast.makeText(this, "Nivel 1 - Sumas básicas", Toast.LENGTH_SHORT).show();

        tv_nombre = findViewById(R.id.textView_nombre);
        tv_score = findViewById(R.id.textView_score);
        iv_vidas = findViewById(R.id.imageView_vidas);
        iv_Auno = findViewById(R.id.imageView_NumUno);
        iv_Ados = findViewById(R.id.imageView_NumDos);
        et_respuesta = findViewById(R.id.editText_resultado);

        nombre_jugador = getIntent().getStringExtra("jugador");
        tv_nombre.setText("Jugador: " + nombre_jugador);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_logo);

        // Obtén la referencia a la ubicación "puntaje" en la base de datos de Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("puntaje");

        // Crea los objetos MediaPlayer
        ////mp = MediaPlayer.create(this, R.raw.goats);
        // MÚSICA DEL JUEGO
        mp = MediaPlayer.create(this, R.raw.juego_normal);
        mp.start();
        mp.setLooping(true);

        mp_great = MediaPlayer.create(this, R.raw.wonderful);
        mp_bad = MediaPlayer.create(this, R.raw.bad);

        NumAleatorio();
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
                        compartirResultado();
                        break;
                }
                et_respuesta.setText("");
            }
            NumAleatorio();
        } else {
            Toast.makeText(this, "Escribe tu respuesta", Toast.LENGTH_SHORT).show();
        }
    }

    private void continuarJuego() {
        Toast.makeText(this, "Has perdido todas tus manzanas", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        mp.stop();
        mp.release();
    }
    private void compartirResultado() {
        mostrarAlertaCompartir();
    }
    private void mostrarAlertaCompartir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Compartir resultados");
        builder.setMessage("¿Deseas compartir tus resultados en WhatsApp?");

        // Botón Compartir
        builder.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Aquí puedes agregar la lógica para compartir los resultados en WhatsApp
                compartirEnWhatsApp();
            }
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                continuarJuego();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void compartirEnWhatsApp() {
        String textoCompartir = "¡Mis resultados son increíbles! puntaje = " + score;

        // Verificar si WhatsApp está instalado en el dispositivo
        if (isWhatsAppInstalled()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, textoCompartir);
            intent.setPackage("com.whatsapp");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // WhatsApp no está instalado o la API de WhatsApp no es compatible
                Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show();
            }
        } else {
            // WhatsApp no está instalado
            Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isWhatsAppInstalled() {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
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
            Intent intent = new Intent(this, MainActivity_Nivel2.class);
            string_score = String.valueOf(score);
            string_vidas = String.valueOf(vidas);
            intent.putExtra("jugador", nombre_jugador);
            intent.putExtra("score", string_score);
            intent.putExtra("vidas", string_vidas);
            startActivity(intent);
            finish();
            //mp.stop();
//            mp.release();
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
}
