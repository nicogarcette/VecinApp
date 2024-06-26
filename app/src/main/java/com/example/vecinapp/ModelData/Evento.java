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
    public String ImageUrl;


    public Evento(String descripcion, String titulo, String nombreUser, String apellidoUser, String IdCategory, GeoPoint direccion, String comunidad,String id, Timestamp fecha,String mail,String image) {
        this.descripcion = descripcion;
        this.titulo = titulo;
        this.nombreUser = nombreUser;
        this.apellidoUser = apellidoUser;
        this.IdCategoria = IdCategory;
        this.direccion = direccion;
        this.comunidad = comunidad;
        this.mail = mail;
        this.fecha = fecha;
        this.id = id;
        this.ImageUrl = image;
    }

    public Evento() {}


}
