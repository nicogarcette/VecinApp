package com.example.vecinapp.ModelData;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Evento {

    private String imagePath;
    public String descripcion;
    public String titulo;
    public String nombreUser;
    public String apellidoUser;
    public String IdCategoria;
    public GeoPoint direccion;
    public String comunidad;
    public String mail;
    public Timestamp fecha;
    public boolean verificado;
    public String id;


    public Evento(String descripcion, String titulo, String nombreUser, String apellidoUser, String IdCategory, GeoPoint direccion, String comunidad, boolean verificado,String id, Timestamp fecha,String mail) {
        this.descripcion = descripcion;
        this.titulo = titulo;
        this.nombreUser = nombreUser;
        this.apellidoUser = apellidoUser;
        this.IdCategoria = IdCategory;
        this.direccion = direccion;
        this.comunidad = comunidad;
        this.verificado = verificado;
        this.mail = mail;
        this.fecha = fecha;
        this.id = id;
    }
    public Evento(String imagePath, String description) {
        this.imagePath = imagePath;
        this.descripcion = description;
    }




    public Evento() {}

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



}
