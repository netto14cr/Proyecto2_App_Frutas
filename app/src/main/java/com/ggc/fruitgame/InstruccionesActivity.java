package com.ggc.fruitgame;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ggc.fruitgame.ui.main.SectionsPagerAdapter;
import com.ggc.fruitgame.databinding.ActivityInstruccionesBinding;

public class InstruccionesActivity extends AppCompatActivity {

    private ActivityInstruccionesBinding binding;
    ViewPager viewPager;
    private LinearLayout linearPuntos;
    private TextView[] puntosSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInstruccionesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        FloatingActionButton fab = binding.fab;

        linearPuntos=findViewById(R.id.idLinearPuntos);
        agregarIndicadorPuntos(0);
        viewPager.addOnPageChangeListener(viewListener);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(InstruccionesActivity.this, "Hola puedo Salir", Toast.LENGTH_SHORT).show();
                Intent miIntent = new Intent(InstruccionesActivity.this, MainActivity.class);
                finish();
            }
        });
    }

    private void agregarIndicadorPuntos(int pos) {
        puntosSlide=new TextView[4];
        linearPuntos.removeAllViews();

        for (int i=0; i<puntosSlide.length; i++){
            puntosSlide[i]=new TextView(this);
            puntosSlide[i].setText(Html.fromHtml("&#8226;"));
            puntosSlide[i].setTextSize(45);
            puntosSlide[i].setTextColor(getResources().getColor(R.color.BlancoTransparente));
            linearPuntos.addView(puntosSlide[i]);
        }
        if (puntosSlide.length>0){
            puntosSlide[pos].setTextColor(getResources().getColor(R.color.purple_700));
        }
    }

    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int pos) {
            agregarIndicadorPuntos(pos);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed(){

    }
}