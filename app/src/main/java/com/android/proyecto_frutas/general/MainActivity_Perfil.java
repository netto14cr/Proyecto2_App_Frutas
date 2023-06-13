package com.android.proyecto_frutas.general;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;

import com.android.proyecto_frutas.R;
import com.android.proyecto_frutas.modelo.NavegacionMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import android.net.Uri;


public class MainActivity_Perfil extends AppCompatActivity {


        private DrawerLayout drawerLayout;
        private ActionBarDrawerToggle actionBarDrawerToggle;
        private static final int REQUEST_IMAGE_CAPTURE = 1;
        private static final int REQUEST_IMAGE_PICK = 2;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_perfil);

            // En tu actividad principal
            NavegacionMenu navegacionMenu = new NavegacionMenu(this, "");
            navegacionMenu.setupNavigationDrawer();
            drawerLayout = findViewById(R.id.drawer_layout);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        public void editarDesdeGaleria(View view) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        }

        public void capturarFoto(View view) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }

    public void defaultImage(View view) {
        ImageView imageView = findViewById(R.id.imageViewPerfil);
        imageView.setImageResource(R.drawable.avatar_01);
        Toast.makeText(this, "Imagen restaurada por defecto", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Aquí puedes actualizar la imagen por la capturada desde la cámara
            ImageView imageView = findViewById(R.id.imageViewPerfil);
            imageView.setImageBitmap(imageBitmap);
            Toast.makeText(this, "Imagen capturada desde la cámara", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                // Aquí puedes actualizar la imagen por la seleccionada desde la galería
                ImageView imageView = findViewById(R.id.imageViewPerfil);
                imageView.setImageBitmap(imageBitmap);
                Toast.makeText(this, "Imagen seleccionada desde la galería", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
