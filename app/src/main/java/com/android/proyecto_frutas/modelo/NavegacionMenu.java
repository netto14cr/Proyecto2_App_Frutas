package com.android.proyecto_frutas.modelo;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.proyecto_frutas.MainActivity;
import com.android.proyecto_frutas.R;
import com.android.proyecto_frutas.general.MainActivity_Perfil;
import com.android.proyecto_frutas.tiempo.MainActivity_Tiempo_Nivel1;
import com.android.proyecto_frutas.tradicional.MainActivity_Nivel1;
import com.google.android.material.navigation.NavigationView;

public class NavegacionMenu {
    private AppCompatActivity activity;
    private String nombre_jugador;

    public NavegacionMenu(AppCompatActivity activity, String nombre_jugador) {
        this.activity = activity;
        this.nombre_jugador = nombre_jugador;
    }

    public void setupNavigationDrawer() {
        NavigationView navigationView = activity.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Manejar los eventos de los elementos del menú lateral aquí
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_inicio:
                        // Acción a realizar cuando se selecciona "Inicio"
                        Intent intentInicio = new Intent(activity, MainActivity.class);
                        activity.startActivity(intentInicio);
                        break;
                    case R.id.menu_modo_tradicional:
                        // Acción a realizar cuando se selecciona "Jugar modo tradicional"
                        Intent intentTradicional = new Intent(activity, MainActivity_Nivel1.class);
                        intentTradicional.putExtra("jugador", nombre_jugador);
                        activity.startActivity(intentTradicional);
                        break;
                    case R.id.menu_modo_tiempo:
                        // Acción a realizar cuando se selecciona "Jugar modo tiempo"
                        Intent intentTiempo = new Intent(activity, MainActivity_Tiempo_Nivel1.class);
                        intentTiempo.putExtra("jugador", nombre_jugador);
                        activity.startActivity(intentTiempo);
                        break;
                    case R.id.menu_perfil:
                        // Acción a realizar cuando se selecciona "Perfil"
                        Intent intentPerfil = new Intent(activity, MainActivity_Perfil.class);
                        activity.startActivity(intentPerfil);
                        break;
                    case R.id.menu_configuraciones:
                        // Acción a realizar cuando se selecciona "Configuraciones"
                        Intent intentConfig = new Intent(activity, MainActivity_Perfil.class);
                        activity.startActivity(intentConfig);
                        break;
                    default:
                        break;
                }

                // Cerrar el menú lateral después de manejar el evento
                DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
}
