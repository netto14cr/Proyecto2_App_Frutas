package com.android.proyecto_frutas.modelo;

public class Puntaje {
    private String nombre;
    private int score;

    public Puntaje() {
        // Constructor vacío requerido para Firebase Realtime Database
    }

    public Puntaje(String nombre, int score) {
        this.nombre = nombre;
        this.score = score;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
