package com.example.vecinapp.ui.evento;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vecinapp.ModelData.Evento;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import java.util.HashMap;
import java.util.Map;
public class EventoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventosRef = db.collection("evento");
    private String currentPhotoPath;
    private MutableLiveData<Boolean> eventoCreado = new MutableLiveData<>();

    public EventoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is eventoooo fragment");
    }

    public void agregarEvento(Evento newEvent) {

        Map<String, Object> evento = new HashMap<>();
        evento.put("nombreUser",newEvent.nombreUser);
        evento.put("descripcion", newEvent.descripcion);
        evento.put("direccion", newEvent.direccion);
        evento.put("titulo", newEvent.titulo);
        evento.put("fecha", Timestamp.now());
        evento.put("IdCategoria", newEvent.IdCategoria);   // nombre de categoria de una
        evento.put("apellidoUser", newEvent.apellidoUser);
        evento.put("comunidad", newEvent.comunidad);
        evento.put("verificado", false);

        eventosRef.add(evento)
                .addOnSuccessListener(documentReference -> {
                    Log.d("EVENTO", "Evento agregado con ID: " + documentReference.getId());
                    eventoCreado.setValue(true);
                })
                .addOnFailureListener(e -> {
                    eventoCreado.setValue(false);
                });
    }

    public LiveData<Boolean> getEventoCreado() {
        return eventoCreado;
    }



    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }

    public LiveData<String> getText() {
        return mText;
    }
}